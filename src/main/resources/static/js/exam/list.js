document.addEventListener('DOMContentLoaded', async function() {
    await loadCategories();
    await loadExams();

    // 검색 이벤트 리스너
    document.getElementById('searchInput').addEventListener('input', filterExams);
});

// 전역 변수
let selectedCategoryId = null;
let exams = [];
let categoriesMap = new Map();

// 카테고리 로드
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');
        const categories = await response.json();
        buildCategoriesMap(categories);
        renderCategoryTree(categories);
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 카테고리 맵 구축 함수
function buildCategoriesMap(categories, parentPath = '') {
    categories.forEach(category => {
        const currentPath = parentPath ? `${parentPath} > ${category.name}` : category.name;
        categoriesMap.set(category.id, currentPath);

        if (category.children && category.children.length > 0) {
            buildCategoriesMap(category.children, currentPath);
        }
    });
}

// 카테고리 트리 렌더링
function renderCategoryTree(categories, parentElement = document.getElementById('categoryTree'), level = 0) {
    parentElement.innerHTML = '';

    // 전체 카테고리 버튼
    if (level === 0) {
        const allCategoryLi = document.createElement('li');
        allCategoryLi.className = 'category-item';
        allCategoryLi.innerHTML = `
            <div class="category-content ${selectedCategoryId === null ? 'selected' : ''}">
                <i class="fas fa-list category-icon"></i>
                <span class="category-name">전체</span>
            </div>
        `;
        parentElement.appendChild(allCategoryLi);

        const divider = document.createElement('li');
        divider.className = 'category-divider';
        parentElement.appendChild(divider);
    }

    categories.forEach(category => {
        const li = document.createElement('li');
        li.className = 'category-item';
        li.setAttribute('data-id', category.id);
        li.setAttribute('data-level', level);

        const hasChildren = category.children && category.children.length > 0;

        li.innerHTML = `
            <div class="category-content ${selectedCategoryId === category.id ? 'selected' : ''}">
                ${hasChildren ?
            '<i class="fas fa-chevron-right category-toggle"></i>' :
            '<i class="fas fa-folder category-icon"></i>'
        }
                <span class="category-name">${category.name}</span>
            </div>
            ${hasChildren ? '<ul class="category-children" style="display: none; height: 0;"</ul>' : ''}
        `;

        parentElement.appendChild(li);

        if (hasChildren) {
            const childrenContainer = li.querySelector('.category-children');
            renderCategoryTree(category.children, childrenContainer, level + 1);
        }
    });
}

// 카테고리 클릭 이벤트 처리
document.addEventListener('click', function(e) {
    // 토글 버튼(화살표) 클릭
    if (e.target.classList.contains('category-toggle')) {
        e.stopPropagation();
        const categoryItem = e.target.closest('.category-item');
        toggleCategory(categoryItem);
    }
    else if (e.target.closest('.category-content')) {
        const categoryItem = e.target.closest('.category-item');
        const categoryId = categoryItem.getAttribute('data-id');
        selectCategory(categoryId ? parseInt(categoryId) : null);
    }
});

// 카테고리 토글 함수
function toggleCategory(categoryItem) {
    const toggle = categoryItem.querySelector('.category-toggle');
    const childrenContainer = categoryItem.querySelector('.category-children');
    const isExpanded = childrenContainer.style.display !== 'none';

    if (isExpanded) {
        collapseCategory(categoryItem);
    } else {
        expandCategory(categoryItem);
    }
}

// 카테고리 펼치기
function expandCategory(categoryItem) {
    const toggle = categoryItem.querySelector('.category-toggle');
    const childrenContainer = categoryItem.querySelector('.category-children');

    childrenContainer.style.display = 'block';
    const targetHeight = childrenContainer.scrollHeight;
    childrenContainer.style.height = '0';

    requestAnimationFrame(() => {
        toggle.classList.add('rotated');
        childrenContainer.style.height = targetHeight + 'px';
    });

    childrenContainer.addEventListener('transitionend', function handler() {
        childrenContainer.style.height = 'auto';
        childrenContainer.removeEventListener('transitionend', handler);
    });
}

// 카테고리 접기
function collapseCategory(categoryItem) {
    const toggle = categoryItem.querySelector('.category-toggle');
    const childrenContainer = categoryItem.querySelector('.category-children');

    toggle.classList.remove('rotated');
    childrenContainer.style.height = childrenContainer.scrollHeight + 'px';
    childrenContainer.offsetHeight; // 강제 리플로우

    childrenContainer.style.height = '0';

    const childCategories = childrenContainer.querySelectorAll('.category-item');
    childCategories.forEach(child => {
        const childToggle = child.querySelector('.category-toggle');
        const childContainer = child.querySelector('.category-children');
        if (childToggle && childContainer) {
            childToggle.classList.remove('rotated');
            childContainer.style.height = '0';
            childContainer.style.display = 'none';
        }
    });

    childrenContainer.addEventListener('transitionend', function handler() {
        childrenContainer.style.display = 'none';
        childrenContainer.removeEventListener('transitionend', handler);
    });
}

// 모의고사 목록 로드
async function loadExams() {
    try {
        // /api/exams 대신 /api/exams/public 사용
        const url = selectedCategoryId ?
            `/api/exams/category/${selectedCategoryId}` :
            '/api/exams/public';

        const response = await fetch(url);
        if (!response.ok) throw new Error('모의고사 로드 실패');

        exams = await response.json();
        renderExams(exams);
    } catch (error) {
        console.error('모의고사 로드 실패:', error);
        showToast('모의고사 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 모의고사 렌더링
function renderExams(examsToRender) {
    const container = document.getElementById('examsList');

    if (examsToRender.length === 0) {
        container.innerHTML = '<div class="no-data">등록된 모의고사가 없습니다.</div>';
        return;
    }

    container.innerHTML = examsToRender.map(exam => `
        <div class="exam-item">
            <div class="exam-info">
                <h3>${exam.title}</h3>
                <p class="exam-description">${exam.description || ''}</p>
                <div class="exam-meta">
                    <span><i class="fas fa-clock"></i> ${exam.timeLimit}분</span>
                    <span><i class="fas fa-list-ol"></i> ${exam.questions.length}문제</span>
                    <span><i class="fas fa-trophy"></i> ${exam.totalPoints}점</span>
                    <span class="category-path">${getCategoryPath(exam.categoryId)}</span>
                </div>
            </div>
            <div class="exam-actions">
                <button class="btn btn-primary btn-sm" onclick="location.href='/exams/${exam.id}'">
                    상세보기
                </button>
            </div>
        </div>
    `).join('');
}

function getCategoryPath(categoryId) {
    return categoriesMap.get(categoryId) || '미분류';
}

// 검색 필터링
function filterExams() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();

    const filteredExams = exams.filter(exam =>
        exam.title.toLowerCase().includes(searchTerm) ||
        (exam.description && exam.description.toLowerCase().includes(searchTerm))
    );

    renderExams(filteredExams);
}

// 카테고리 선택
function selectCategory(categoryId) {
    selectedCategoryId = categoryId;
    loadExams();

    document.querySelectorAll('.category-content').forEach(el => {
        el.classList.remove('selected');
    });

    const selectedElement = categoryId ?
        document.querySelector(`[data-id="${categoryId}"] .category-content`) :
        document.querySelector('.category-item:first-child .category-content');

    if (selectedElement) {
        selectedElement.classList.add('selected');
    }
}

// 토스트 메시지
function showToast(message, type = 'success') {
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