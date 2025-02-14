document.addEventListener('DOMContentLoaded', function() {
    loadCategories();

    // 검색 및 필터링 이벤트 리스너
    document.getElementById('searchInput').addEventListener('input', filterQuestions);
    document.getElementById('typeFilter').addEventListener('change', filterQuestions);
    document.getElementById('categoryFilter').addEventListener('change', filterQuestions);
});

// 전역 변수 - 초기 렌더링된 문제들을 저장
let myQuestions = Array.from(document.querySelectorAll('.question-card')).map(card => ({
    id: card.dataset.id,
    title: card.querySelector('.question-title').textContent,
    typeId: card.dataset.typeId,
    categoryId: card.dataset.categoryId,
    categoryName: card.querySelector('.category-path').textContent.trim(),
    difficulty: parseInt(card.dataset.difficulty),
    createdAt: card.dataset.createdAt
}));

// 카테고리 로드
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');
        const categories = await response.json();
        renderCategoryOptions(categories);
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 카테고리 옵션 렌더링
function renderCategoryOptions(categories, level = 0) {
    const select = document.getElementById('categoryFilter');
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = '　'.repeat(level) + category.name;
        select.appendChild(option);

        if (category.children && category.children.length > 0) {
            renderCategoryOptions(category.children, level + 1);
        }
    });
}

// 필터링 함수
function filterQuestions() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const categoryFilter = document.getElementById('categoryFilter').value;

    document.querySelectorAll('.question-card').forEach(card => {
        const matchesSearch = card.querySelector('.question-title').textContent.toLowerCase().includes(searchTerm);
        const matchesType = !typeFilter || card.dataset.typeId === typeFilter;
        const matchesCategory = !categoryFilter || card.dataset.categoryId === categoryFilter;

        card.style.display = matchesSearch && matchesType && matchesCategory ? 'block' : 'none';
    });

    // 필터링 결과가 없는 경우 메시지 표시
    const visibleCards = document.querySelectorAll('.question-card[style="display: block"]');
    const container = document.getElementById('myQuestionsList');

    if (visibleCards.length === 0) {
        const noResults = document.querySelector('.no-results') || document.createElement('div');
        noResults.className = 'no-results';
        noResults.textContent = '검색 결과가 없습니다.';
        container.appendChild(noResults);
    } else {
        const noResults = document.querySelector('.no-results');
        if (noResults) noResults.remove();
    }
}

// 삭제 확인
function confirmDelete(questionId) {
    if (confirm('정말 이 문제를 삭제하시겠습니까?\n삭제된 문제는 복구할 수 없습니다.')) {
        deleteQuestion(questionId);
    }
}

// 문제 삭제
async function deleteQuestion(questionId) {
    try {
        showLoading();
        const response = await fetch(`/api/questions/${questionId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('문제 삭제 실패');

        showToast('문제가 삭제되었습니다.');
        // 삭제된 카드 제거
        const card = document.querySelector(`.question-card[data-id="${questionId}"]`);
        if (card) card.remove();
    } catch (error) {
        console.error('문제 삭제 실패:', error);
        showToast('문제 삭제에 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}

// 유틸리티 함수들
function showLoading() {
    const loading = document.querySelector('.loading') || createLoadingElement();
    loading.style.display = 'block';
}

function hideLoading() {
    const loading = document.querySelector('.loading');
    if (loading) {
        loading.style.display = 'none';
    }
}

function createLoadingElement() {
    const loading = document.createElement('div');
    loading.className = 'loading';
    document.body.appendChild(loading);
    return loading;
}

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