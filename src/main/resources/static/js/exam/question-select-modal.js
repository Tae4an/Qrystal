// question-select-modal.js

// 전역 변수
let modalSelectedCategoryId = null;
let modalQuestions = [];
let modalSelectedQuestions = [];
let modalCategoriesMap = new Map();

// 모달 표시/숨김 함수
function showQuestionModal() {
    const modal = document.getElementById('questionSelectModal');
    modal.classList.add('show');
    modalSelectedQuestions = [...selectedQuestions];
    loadModalCategories();
    updateModalSelectedQuestions();
}

function hideQuestionModal() {
    const modal = document.getElementById('questionSelectModal');
    modal.classList.remove('show');
}

// 카테고리 로드 함수
async function loadModalCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');
        const categories = await response.json();
        buildModalCategoriesMap(categories);
        renderModalCategoryTree(categories);
        await loadModalQuestions();
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 카테고리 맵 구축
function buildModalCategoriesMap(categories, parentPath = '') {
    categories.forEach(category => {
        const currentPath = parentPath ? `${parentPath} > ${category.name}` : category.name;
        modalCategoriesMap.set(category.id, currentPath);

        if (category.children && category.children.length > 0) {
            buildModalCategoriesMap(category.children, currentPath);
        }
    });
}

// 카테고리 트리 렌더링
function renderModalCategoryTree(categories, parentElement = document.getElementById('modalCategoryTree'), level = 0) {
    parentElement.innerHTML = '';

    // 전체 카테고리 버튼
    if (level === 0) {
        const allCategoryLi = document.createElement('li');
        allCategoryLi.className = 'category-item';
        allCategoryLi.innerHTML = `
           <div class="category-content ${modalSelectedCategoryId === null ? 'selected' : ''}">
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
           <div class="category-content ${modalSelectedCategoryId === category.id ? 'selected' : ''}">
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
            renderModalCategoryTree(category.children, childrenContainer, level + 1);
        }
    });
}

// 모달 내 문제 목록 로드
async function loadModalQuestions() {
    try {
        const url = modalSelectedCategoryId ?
            `/api/questions/category/${modalSelectedCategoryId}` :
            '/api/questions';

        const response = await fetch(url);
        if (!response.ok) throw new Error('문제 로드 실패');

        modalQuestions = await response.json();
        renderModalQuestions(modalQuestions);

    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 모달 내 문제 목록 렌더링
function renderModalQuestions(questions) {
    const container = document.getElementById('modalQuestionsList');
    container.innerHTML = questions.map(question => `
       <div class="question-item ${isQuestionSelected(question.id) ? 'disabled' : ''}" 
            data-id="${question.id}">
           <div class="question-header" onclick="toggleModalQuestion(${question.id})">
               <div class="question-title">${question.title}</div>
               <div class="question-meta">
                   <span class="category-path">${getCategoryPath(question.categoryId)}</span>
                   <span class="question-type">${getQuestionType(question.typeId)}</span>
                   <span class="question-difficulty">난이도 ${getQuestionDifficulty(question.difficulty)}</span>
               </div>
           </div>
           <div id="modal-detail-${question.id}" class="question-detail">
               <div class="detail-content">
                   ${renderModalQuestionContent(question)}
               </div>
           </div>
           ${!isQuestionSelected(question.id) ? `
               <button type="button" class="btn btn-sm btn-primary add-question-btn" 
                       onclick="addModalQuestion(${question.id}, '${question.title}')">
                   <i class="fas fa-plus"></i>
               </button>
           ` : ''}
       </div>
   `).join('');
}

// 카테고리 클릭 이벤트 처리
document.getElementById('modalCategoryTree')?.addEventListener('click', function(e) {
    if (e.target.classList.contains('category-toggle')) {
        e.stopPropagation();
        const categoryItem = e.target.closest('.category-item');
        toggleModalCategory(categoryItem);
    }
    else if (e.target.closest('.category-content')) {
        const categoryItem = e.target.closest('.category-item');
        const categoryId = categoryItem.getAttribute('data-id');
        selectModalCategory(categoryId ? parseInt(categoryId) : null);
    }
});

// 카테고리 토글 함수
function toggleModalCategory(categoryItem) {
    const toggle = categoryItem.querySelector('.category-toggle');
    const childrenContainer = categoryItem.querySelector('.category-children');
    const isExpanded = childrenContainer.style.display !== 'none';

    if (isExpanded) {
        collapseModalCategory(categoryItem);
    } else {
        expandModalCategory(categoryItem);
    }
}

// 카테고리 펼치기
function expandModalCategory(categoryItem) {
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
function collapseModalCategory(categoryItem) {
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

// 카테고리 선택
function selectModalCategory(categoryId) {
    modalSelectedCategoryId = categoryId;
    loadModalQuestions();

    document.querySelectorAll('#modalCategoryTree .category-content').forEach(el => {
        el.classList.remove('selected');
    });

    if (categoryId === null) {
        document.querySelector('#modalCategoryTree .category-item:first-child .category-content').classList.add('selected');
    } else {
        const selectedElement = document.querySelector(`#modalCategoryTree [data-id="${categoryId}"] .category-content`);
        if (selectedElement) {
            selectedElement.classList.add('selected');
        }
    }
}

// 문제 상세보기 토글
function toggleModalQuestion(questionId) {
    const detailElement = document.getElementById(`modal-detail-${questionId}`);

    // 이미 펼쳐진 다른 문제들을 접기
    document.querySelectorAll('.question-detail.expanded').forEach(el => {
        if (el.id !== `modal-detail-${questionId}`) {
            el.classList.remove('expanded');
        }
    });

    detailElement.classList.toggle('expanded');
}

// 문제 선택 여부 확인
function isQuestionSelected(questionId) {
    return modalSelectedQuestions.some(q => q.questionId === questionId);
}

// 문제 추가
function addModalQuestion(questionId, title) {
    if (isQuestionSelected(questionId)) return;

    const newQuestion = {
        questionId: questionId,
        questionNumber: modalSelectedQuestions.length + 1,
        point: 5,
        title: title
    };

    modalSelectedQuestions.push(newQuestion);
    updateModalSelectedQuestions();
    renderModalQuestions(modalQuestions);
}

// 문제 제거
function removeModalQuestion(index) {
    modalSelectedQuestions.splice(index, 1);
    modalSelectedQuestions.forEach((q, i) => {
        q.questionNumber = i + 1;
    });
    updateModalSelectedQuestions();
    renderModalQuestions(modalQuestions);
}

// 선택된 문제 목록 업데이트
function updateModalSelectedQuestions() {
    const container = document.getElementById('modalSelectedQuestions');
    const countElement = document.querySelector('.selected-count');

    countElement.textContent = `${modalSelectedQuestions.length}개 선택됨`;

    container.innerHTML = modalSelectedQuestions.map((question, index) => `
       <div class="selected-question-item">
           <div class="question-info">
               <span class="question-number">${index + 1}</span>
               <span class="question-title">${question.title}</span>
           </div>
           <div class="question-actions">
               <input type="number" class="point-input" value="${question.point}"
                      onchange="updateModalPoint(${index}, this.value)" min="1">
               <button type="button" class="btn btn-sm btn-danger"
                       onclick="removeModalQuestion(${index})">
                   <i class="fas fa-minus"></i>
               </button>
           </div>
       </div>
   `).join('');
}

// 배점 수정
function updateModalPoint(index, value) {
    modalSelectedQuestions[index].point = parseInt(value);
}

// 선택 완료
function applySelectedQuestions() {
    selectedQuestions = [...modalSelectedQuestions];
    updateSelectedQuestionsUI();
    hideQuestionModal();
}

// 검색 기능
document.getElementById('modalSearchInput')?.addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    const filteredQuestions = modalQuestions.filter(question =>
        question.title.toLowerCase().includes(searchTerm)
    );
    renderModalQuestions(filteredQuestions);
});

// 문제 유형별 렌더링
function renderModalQuestionContent(question) {
    let content = `
       <div class="question-content">
           ${question.content}
       </div>
   `;

    switch(parseInt(question.typeId)) {
        case 1:
            content += renderModalMultipleChoice(question);
            break;
        case 2:
            content += `<div class="answer">정답: ${question.answer}</div>`;
            break;
        case 3:
            content += `<div class="answer">모범답안: ${question.answer}</div>`;
            break;
    }

    if (question.explanation) {
        content += `
           <div class="explanation">
               <h4>해설</h4>
               ${question.explanation}
           </div>
       `;
    }

    return content;
}

// 객관식 문제 보기 렌더링
function renderModalMultipleChoice(question) {
    return `
       <div class="choices-list">
           ${question.choices.map(choice => `
               <div class="choice-item">
                   <span class="choice-number">${choice.choiceNumber}</span>
                   <span class="choice-content">${choice.content}</span>
                   ${choice.choiceNumber === question.answer ?
        '<span class="correct-answer">(정답)</span>' : ''}
               </div>
           `).join('')}
       </div>
   `;
}

// 카테고리 경로 가져오기
function getCategoryPath(categoryId) {
    return modalCategoriesMap.get(categoryId) || '미분류';
}

// 난이도 표시 함수
function getQuestionDifficulty(difficulty) {
    const maxDifficulty = 5;
    const filledStars = '★'.repeat(difficulty);
    const emptyStars = '☆'.repeat(maxDifficulty - difficulty);
    return filledStars + emptyStars;
}

// 문제 유형 반환
function getQuestionType(typeId) {
    const types = {
        1: '객관식',
        2: '주관식',
        3: '서술형'
    };
    return types[typeId] || '알 수 없음';
}