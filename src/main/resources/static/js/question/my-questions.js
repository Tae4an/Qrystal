// my-questions.js
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();
    loadMyQuestions();

    // 검색 및 필터링 이벤트 리스너
    document.getElementById('searchInput').addEventListener('input', filterQuestions);
    document.getElementById('typeFilter').addEventListener('change', filterQuestions);
    document.getElementById('categoryFilter').addEventListener('change', filterQuestions);
});

// 전역 변수
let myQuestions = [];

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

// 내 문제 목록 로드
async function loadMyQuestions() {
    try {
        showLoading();
        const response = await fetch('/api/questions/my');
        if (!response.ok) throw new Error('문제 로드 실패');
        myQuestions = await response.json();
        renderQuestions(myQuestions);
    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 목록을 불러오는데 실패했습니다.', 'error');
    } finally {
        hideLoading();
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

// 문제 목록 렌더링
function renderQuestions(questionsToRender = myQuestions) {
    const container = document.getElementById('myQuestionsList');
    if (questionsToRender.length === 0) {
        container.innerHTML = '<div class="no-questions">등록한 문제가 없습니다.</div>';
        return;
    }

    container.innerHTML = questionsToRender.map(question => `
       <div class="question-card">
           <div class="question-header">
               <span class="question-title">${question.title}</span>
           </div>
           <div class="question-info">
               <div class="question-meta">
                   <span class="badge badge-category">${question.categoryName}</span>
                   <span class="badge badge-type">${getQuestionType(question.typeId)}</span>
                   <span>난이도: ${'★'.repeat(question.difficulty)}</span>
                   <span>작성일: ${formatDate(question.createdAt)}</span>
               </div>
               <div class="question-actions">
                   <button class="btn btn-edit" onclick="editQuestion(${question.id})">
                       <i class="fas fa-edit"></i> 수정
                   </button>
                   <button class="btn btn-delete" onclick="confirmDelete(${question.id})">
                       <i class="fas fa-trash"></i> 삭제
                   </button>
               </div>
           </div>
       </div>
   `).join('');
}

// 문제 수정 페이지로 이동
function editQuestion(questionId) {
    location.href = `/questions/${questionId}/edit`;
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
        loadMyQuestions();  // 목록 새로고침
    } catch (error) {
        console.error('문제 삭제 실패:', error);
        showToast('문제 삭제에 실패했습니다.', 'error');
    } finally {
        hideLoading();
    }
}

// 검색 및 필터링
function filterQuestions() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const categoryFilter = document.getElementById('categoryFilter').value;

    const filteredQuestions = myQuestions.filter(question => {
        const matchesSearch = question.title.toLowerCase().includes(searchTerm);
        const matchesType = !typeFilter || question.typeId.toString() === typeFilter;
        const matchesCategory = !categoryFilter || question.categoryId.toString() === categoryFilter;

        return matchesSearch && matchesType && matchesCategory;
    });

    renderQuestions(filteredQuestions);
}

// 유틸리티 함수들
function getQuestionType(typeId) {
    const types = {
        1: '객관식',
        2: '주관식',
        3: '서술형'
    };
    return types[typeId] || '알 수 없음';
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(date);
}

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