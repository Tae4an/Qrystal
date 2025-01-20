// list.js
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();
    loadQuestions();
});

// 전역 변수
let selectedCategoryId = null;

// 카테고리 관련 함수
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');
        const categories = await response.json();
        renderCategoryTree(categories);
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}

function renderCategoryTree(categories, parentElement = document.getElementById('categoryTree')) {
    parentElement.innerHTML = '';
    categories.forEach(category => {
        const li = document.createElement('li');
        li.className = 'category-item';
        li.innerHTML = `
           <div class="category-content ${selectedCategoryId === category.id ? 'selected' : ''}" 
                onclick="selectCategory(${category.id})">
               <i class="fas fa-folder"></i>
               <span>${category.name}</span>
           </div>
       `;

        if (category.children && category.children.length > 0) {
            const ul = document.createElement('ul');
            ul.className = 'category-children';
            renderCategoryTree(category.children, ul);
            li.appendChild(ul);
        }

        parentElement.appendChild(li);
    });
}

function selectCategory(categoryId) {
    selectedCategoryId = categoryId;
    loadQuestions();
    // 선택된 카테고리 스타일 업데이트
    document.querySelectorAll('.category-content').forEach(el => {
        el.classList.remove('selected');
    });
    event.currentTarget.classList.add('selected');
}

// 문제 관련 함수
async function loadQuestions() {
    try {
        let url = '/api/questions';
        if (selectedCategoryId) {
            url += `/category/${selectedCategoryId}`;
        }

        // 검색 필터 적용
        const searchParams = new URLSearchParams();
        const searchInput = document.getElementById('searchInput').value;
        const typeFilter = document.getElementById('typeFilter').value;
        const difficultyFilter = document.getElementById('difficultyFilter').value;

        if (searchInput) searchParams.append('search', searchInput);
        if (typeFilter) searchParams.append('typeId', typeFilter);
        if (difficultyFilter) searchParams.append('difficulty', difficultyFilter);

        if (searchParams.toString()) {
            url += '?' + searchParams.toString();
        }

        const response = await fetch(url);
        if (!response.ok) throw new Error('문제 로드 실패');
        const questions = await response.json();
        renderQuestionList(questions);
    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 목록을 불러오는데 실패했습니다.', 'error');
    }
}

function renderQuestionList(questions) {
    const tbody = document.getElementById('questionList');
    tbody.innerHTML = '';

    questions.forEach(question => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
           <td>
               <a href="/questions/${question.id}" class="question-title">
                   ${question.title}
               </a>
           </td>
           <td>${question.typeName}</td>
           <td>${renderDifficulty(question.difficulty)}</td>
           <td>${question.userName}</td>
           <td>${formatDate(question.createdAt)}</td>
           <td>
               <span class="status-badge status-${question.status.toLowerCase()}">
                   ${getStatusText(question.status)}
               </span>
           </td>
           <td class="action-buttons">
               <button class="btn btn-primary btn-sm" onclick="location.href='/questions/${question.id}/edit'">
                   <i class="fas fa-edit"></i>
               </button>
               <button class="btn btn-danger btn-sm" onclick="deleteQuestion(${question.id})">
                   <i class="fas fa-trash"></i>
               </button>
           </td>
       `;
        tbody.appendChild(tr);
    });
}

// 유틸리티 함수들
function renderDifficulty(level) {
    return '★'.repeat(level);
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

function getStatusText(status) {
    const statusMap = {
        'ACTIVE': '활성',
        'INACTIVE': '비활성',
        'REPORTED': '신고됨'
    };
    return statusMap[status] || status;
}

// 문제 삭제
async function deleteQuestion(questionId) {
    if (!confirm('정말 이 문제를 삭제하시겠습니까?')) return;

    try {
        const response = await fetch(`/api/questions/${questionId}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('문제 삭제 실패');

        showToast('문제가 삭제되었습니다.');
        loadQuestions();
    } catch (error) {
        console.error('문제 삭제 실패:', error);
        showToast('문제 삭제에 실패했습니다.', 'error');
    }
}

// 검색 및 필터링 이벤트 리스너
document.getElementById('searchInput').addEventListener('input', _.debounce(loadQuestions, 300));
document.getElementById('typeFilter').addEventListener('change', loadQuestions);
document.getElementById('difficultyFilter').addEventListener('change', loadQuestions);

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