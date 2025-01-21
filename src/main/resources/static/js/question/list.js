// list.js
document.addEventListener('DOMContentLoaded', function() {
    loadCategories();
    loadQuestions();

    // 검색 및 필터링 이벤트 리스너
    document.getElementById('searchInput').addEventListener('input', filterQuestions);
    document.getElementById('typeFilter').addEventListener('change', filterQuestions);
    document.getElementById('difficultyFilter').addEventListener('change', filterQuestions);
});

// 전역 변수
let selectedCategoryId = null;
let questions = [];

// 카테고리 로드
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

// 카테고리 트리 렌더링
function renderCategoryTree(categories, parentElement = document.getElementById('categoryTree')) {
    parentElement.innerHTML = '';

    categories.forEach(category => {
        const li = document.createElement('li');
        li.className = 'category-item';
        li.setAttribute('data-id', category.id);

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

// 카테고리 선택
function selectCategory(categoryId) {
    selectedCategoryId = categoryId;
    loadQuestions();

    // 선택된 카테고리 스타일 업데이트
    document.querySelectorAll('.category-content').forEach(el => {
        el.classList.remove('selected');
    });
    document.querySelector(`[data-id="${categoryId}"] .category-content`).classList.add('selected');
}

// 문제 목록 로드
async function loadQuestions() {
    try {
        const url = selectedCategoryId ?
            `/api/questions/category/${selectedCategoryId}` :
            '/api/questions';

        const response = await fetch(url);
        if (!response.ok) throw new Error('문제 로드 실패');

        questions = await response.json();
        renderQuestions(questions);
    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 문제 목록 렌더링
function renderQuestions(questionsToRender = questions) {
    const container = document.getElementById('questionsList');
    container.innerHTML = questionsToRender.map(question => `
        <div class="question-card" data-id="${question.id}">
            <div class="question-card-header" onclick="toggleQuestion(${question.id})">
                <div class="question-title">${question.title}</div>
                <div class="question-meta">
                    <span>${getQuestionType(question.typeId)}</span>
                    <span>난이도: ${'★'.repeat(question.difficulty)}</span>
                </div>
            </div>
            <div id="detail-${question.id}" class="question-detail">
                <div class="detail-content">
                    ${renderQuestionContent(question)}
                </div>
            </div>
        </div>
    `).join('');
}

// 문제 유형별 렌더링
function renderQuestionContent(question) {
    console.log('Question type:', question.typeId, typeof question.typeId);  // typeId 값과 타입 확인
    let content = `
        <div class="question-content">
            ${question.content}
        </div>
    `;

    const typeId = parseInt(question.typeId);
    console.log('Parsed typeId:', typeId);  // 파싱된 typeId 확인

    // 문제 유형별 입력 영역
    switch(typeId) {
        case 1: // 객관식
            console.log('Entering case 1');  // switch문 진입 확인
            content += renderMultipleChoice(question);
            break;
        case 2: // 주관식
            content += renderShortAnswer();
            break;
        case 3: // 서술형
            content += renderLongAnswer();
            break;
        default:
            console.log('Unknown question type:', typeId);  // 알 수 없는 타입인 경우
    }

    content += `
        <button class="check-answer-btn" onclick="checkAnswer(${question.id})">
            정답 확인
        </button>
        <div id="result-${question.id}" class="answer-result"></div>
        <div id="explanation-${question.id}" class="explanation">
            <h4>해설</h4>
            ${question.explanation || '해설이 없습니다.'}
        </div>
    `;

    return content;
}

// 객관식 문제 렌더링
function renderMultipleChoice(question) {
    console.log('Rendering choices:', question.choices);
    return `
        <div class="multiple-choice-list">
            ${question.choices.sort((a, b) => a.choiceNumber - b.choiceNumber)
        .map(choice => `
                    <label class="choice-item">
                        <input type="radio" name="question-${question.id}" value="${choice.choiceNumber}">
                        <span>${choice.content}</span>
                    </label>
                `).join('')}
        </div>
    `;
}

// 주관식 문제 렌더링
function renderShortAnswer() {
    return `
        <input type="text" class="text-answer-input short" placeholder="답안을 입력하세요">
    `;
}

// 서술형 문제 렌더링
function renderLongAnswer() {
    return `
        <textarea class="text-answer-input long" placeholder="답안을 입력하세요"></textarea>
    `;
}

// 문제 펼치기/접기
function toggleQuestion(questionId) {
    const detailElement = document.getElementById(`detail-${questionId}`);

    // 이미 펼쳐진 다른 문제들을 접기
    document.querySelectorAll('.question-detail.expanded').forEach(el => {
        if (el.id !== `detail-${questionId}`) {
            el.classList.remove('expanded');
        }
    });

    detailElement.classList.toggle('expanded');
}

// 정답 확인
function checkAnswer(questionId) {
    const question = questions.find(q => q.id === questionId);
    const resultElement = document.getElementById(`result-${questionId}`);
    const explanationElement = document.getElementById(`explanation-${questionId}`);
    let userAnswer;

    // 문제 유형별 답안 가져오기
    switch(parseInt(question.typeId)) {
        case 1: // 객관식
            userAnswer = document.querySelector(`input[name="question-${questionId}"]:checked`)?.value;
            break;
        case 2: // 주관식
        case 3: // 서술형
            userAnswer = document.querySelector(`#detail-${questionId} .text-answer-input`).value;
            break;
    }

    if (!userAnswer) {
        alert('답안을 입력해주세요.');
        return;
    }

    // 정답 체크
    let isCorrect = false;
    switch(parseInt(question.typeId)) {
        case 1: // 객관식
            isCorrect = userAnswer === question.answer;
            break;
        case 2: // 주관식
            isCorrect = userAnswer.trim().toLowerCase() === question.answer.trim().toLowerCase();
            break;
        case 3: // 서술형
            // 서술형은 모범답안만 표시
            isCorrect = true;
            break;
    }

    // 결과 표시
    resultElement.className = `answer-result ${isCorrect ? 'correct' : 'incorrect'}`;
    resultElement.innerHTML = isCorrect ?
        '<strong>정답입니다!</strong>' :
        `<strong>틀렸습니다.</strong><br>정답: ${question.answer}`;
    resultElement.style.display = 'block';

    // 해설 표시
    explanationElement.style.display = 'block';

    // 입력 비활성화
    disableAnswerInput(questionId);
}

// 답안 입력 비활성화
function disableAnswerInput(questionId) {
    const detailElement = document.getElementById(`detail-${questionId}`);
    const inputs = detailElement.querySelectorAll('input, textarea');
    inputs.forEach(input => input.disabled = true);
}

// 검색 및 필터링
function filterQuestions() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const difficultyFilter = document.getElementById('difficultyFilter').value;

    const filteredQuestions = questions.filter(question => {
        const matchesSearch = question.title.toLowerCase().includes(searchTerm);
        const matchesType = !typeFilter || question.typeId.toString() === typeFilter;
        const matchesDifficulty = !difficultyFilter || question.difficulty.toString() === difficultyFilter;

        return matchesSearch && matchesType && matchesDifficulty;
    });

    renderQuestions(filteredQuestions);
}

// 문제 유형 텍스트 반환
function getQuestionType(typeId) {
    const types = {
        1: '객관식',
        2: '주관식',
        3: '서술형'
    };
    return types[typeId] || '알 수 없음';
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