// 전역 변수
let categories = [];

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();

    // 폼 제출 이벤트 리스너 등록
    document.getElementById('categoryForm').addEventListener('submit', saveCategory);
});

// 카테고리 목록 로드
async function loadCategories() {
    try {
        showLoading();
        const response = await fetch('/api/admin/categories');
        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }
        const data = await response.json();
        console.log('서버 응답 데이터:', data);  // 데이터 구조 확인
        categories = data;
        renderCategoryTree();
        updateParentSelect();
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}

// 카테고리 트리 렌더링
function renderCategoryTree() {
    const rootElement = document.getElementById('categoryList');
    rootElement.innerHTML = ''; // 기존 내용 초기화

    categories.forEach(category => {
        const categoryElement = createCategoryElement(category);
        rootElement.appendChild(categoryElement);
    });
}


// 카테고리 엘리먼트 생성 함수
function createCategoryElement(category) {
    const li = document.createElement('li');
    li.className = 'tree-item';
    li.setAttribute('data-id', category.id);

    // 카테고리 레벨에 따른 들여쓰기 스타일 추가
    const levelClass = `level-${category.level}`;

    li.innerHTML = `
        <div class="tree-item-content ${levelClass}">
            <i class="fas fa-folder"></i>
            <span class="tree-item-name">${category.name}</span>
            <div class="tree-item-actions">
                <button type="button" class="btn btn-sm ${category.status === 'ACTIVE' ? 'btn-success' : 'btn-secondary'}"
                        onclick="toggleStatus(${category.id})">
                    <i class="fas ${category.status === 'ACTIVE' ? 'fa-check' : 'fa-times'}"></i>
                </button>
                <button type="button" class="btn btn-sm btn-primary" onclick="editCategory(${category.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button type="button" class="btn btn-sm btn-danger" onclick="deleteCategory(${category.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `;

    // children 배열이 있고 비어있지 않은 경우
    if (category.children && category.children.length > 0) {
        const childrenContainer = document.createElement('ul');
        childrenContainer.className = 'tree-children';
        category.children.forEach(childCategory => {
            // 자식 카테고리도 같은 함수로 생성
            const childElement = createCategoryElement(childCategory);
            childrenContainer.appendChild(childElement);
        });
        li.appendChild(childrenContainer);
    }

    return li;
}

// Modal 관련 함수
function openCategoryModal(isEdit = false) {
    const modal = document.getElementById('categoryModal');
    if (!modal) return;

    document.getElementById('modalTitle').textContent = isEdit ? '카테고리 수정' : '새 카테고리';

    if (!isEdit) {
        document.getElementById('categoryForm').reset();
        document.getElementById('categoryId').value = '';
    }

    modal.style.display = 'block';
    setTimeout(() => {
        modal.classList.add('show');
    }, 10);
}

function closeCategoryModal() {
    const modal = document.getElementById('categoryModal');
    if (!modal) return;

    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
    }, 300);
}

// 카테고리 저장 (생성/수정)
async function saveCategory(event) {
    event.preventDefault();

    const formData = {
        name: document.getElementById('name').value,
        parentId: document.getElementById('parentId').value || null,
        description: document.getElementById('description').value,
        status: document.getElementById('status').value
    };

    const categoryId = document.getElementById('categoryId').value;

    try {
        showLoading();
        let response;

        if (categoryId) {
            // 수정
            response = await fetch(`/api/admin/categories/${categoryId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
        } else {
            // 생성
            response = await fetch('/api/admin/categories', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
        }

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        await loadCategories();
        closeCategoryModal();
        showToast(categoryId ? '카테고리가 수정되었습니다.' : '카테고리가 생성되었습니다.');
    } catch (error) {
        console.error('카테고리 저장 실패:', error);
        showToast('카테고리 저장에 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}

// 카테고리 삭제
async function deleteCategory(categoryId) {
    if (!confirm('정말 이 카테고리를 삭제하시겠습니까?')) return;

    try {
        showLoading();
        const response = await fetch(`/api/admin/categories/${categoryId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        await loadCategories();
        showToast('카테고리가 삭제되었습니다.');
    } catch (error) {
        console.error('카테고리 삭제 실패:', error);
        showToast('카테고리 삭제에 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}

// 카테고리 수정 모달
function editCategory(categoryId) {
    // 재귀적으로 카테고리 찾기
    const category = findCategoryRecursive(categories, categoryId);
    if (!category) {
        showToast('카테고리를 찾을 수 없습니다.', 'error');
        return;
    }

    document.getElementById('categoryId').value = category.id;
    document.getElementById('parentId').value = category.parentId || '';
    document.getElementById('name').value = category.name;
    document.getElementById('description').value = category.description || '';
    document.getElementById('status').value = category.status;

    openCategoryModal(true);
}

// 카테고리 상태 토글
async function toggleStatus(categoryId) {
    try {
        showLoading();
        // 현재 카테고리 찾기 - 재귀적으로 모든 하위 카테고리도 검색
        const category = findCategoryRecursive(categories, categoryId);
        if (!category) {
            throw new Error('카테고리를 찾을 수 없습니다.');
        }

        const newStatus = category.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';

        const response = await fetch(`/api/admin/categories/${categoryId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: categoryId,
                name: category.name,
                description: category.description,
                status: newStatus
            })
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }

        await loadCategories();
        showToast('카테고리 상태가 변경되었습니다.');
    } catch (error) {
        console.error('상태 변경 실패:', error);
        showToast('상태 변경에 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}
// 재귀적으로 카테고리 찾기
function findCategoryRecursive(categories, id) {
    for (const category of categories) {
        if (category.id === id) {
            return category;
        }
        if (category.children && category.children.length > 0) {
            const found = findCategoryRecursive(category.children, id);
            if (found) return found;
        }
    }
    return null;
}

// 유틸리티 함수들
function updateParentSelect() {
    const select = document.getElementById('parentId');
    select.innerHTML = '<option value="">최상위 카테고리</option>';

    function addOptions(categories, level = 0) {
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category.id;
            option.textContent = '　'.repeat(level) + category.name;
            select.appendChild(option);

            if (category.children) {
                addOptions(category.children, level + 1);
            }
        });
    }

    addOptions(categories);
}

function showLoading() {
    const spinner = document.querySelector('.loading-spinner');
    if (spinner) spinner.style.display = 'block';
}

function hideLoading() {
    const spinner = document.querySelector('.loading-spinner');
    if (spinner) spinner.style.display = 'none';
}

// Toast 메시지
function showToast(message, type = 'success') {
    const existingToast = document.querySelector('.toast');
    if (existingToast) {
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
       <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
       <span>${message}</span>
   `;

    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 10);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('categoryModal');
    if (event.target === modal) {
        closeCategoryModal();
    }
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeCategoryModal();
    }
});