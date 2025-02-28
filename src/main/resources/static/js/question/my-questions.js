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
    categoryName: card.querySelector('.category-path span').textContent.trim(),
    difficulty: parseInt(card.dataset.difficulty),
    createdAt: card.dataset.createdAt
}));

// 상세 문제 데이터를 저장할 맵
let questionsDetailMap = new Map();

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

// 문제 토글 기능
function toggleQuestion(questionId) {
    const detailElement = document.getElementById(`detail-${questionId}`);

    // 이미 펼쳐진 다른 문제들을 접기
    document.querySelectorAll('.question-detail.expanded').forEach(el => {
        if (el.id !== `detail-${questionId}`) {
            el.classList.remove('expanded');
        }
    });

    // 현재 문제 토글
    const isExpanding = !detailElement.classList.contains('expanded');
    detailElement.classList.toggle('expanded');

    // 문제 상세 정보 로드 (펼칠 때만)
    if (isExpanding) {
        loadQuestionDetail(questionId);
    }
}

// 문제 상세 정보 로드
async function loadQuestionDetail(questionId) {
    const detailElement = document.getElementById(`detail-${questionId}`);
    const detailContent = detailElement.querySelector('.detail-content');

    // 이미 로드된 데이터가 있으면 재사용
    if (questionsDetailMap.has(questionId)) {
        renderQuestionDetail(questionsDetailMap.get(questionId), detailContent);
        return;
    }

    try {
        // 로딩 표시
        detailContent.innerHTML = `
            <div class="detail-loader">
                <div class="spinner"></div>
                <p>문제를 불러오는 중...</p>
            </div>
        `;

        const response = await fetch(`/api/questions/${questionId}`);
        if (!response.ok) throw new Error('문제 상세 정보 로드 실패');

        const questionDetail = await response.json();
        questionsDetailMap.set(questionId, questionDetail);

        renderQuestionDetail(questionDetail, detailContent);
    } catch (error) {
        console.error('문제 상세 정보 로드 실패:', error);
        detailContent.innerHTML = `
            <div class="error-message">
                <i class="fas fa-exclamation-circle"></i>
                <p>문제를 불러오는데 실패했습니다. 다시 시도해주세요.</p>
            </div>
        `;
    }
}

// 문제 상세 정보 렌더링
function renderQuestionDetail(question, container) {
    let content = `
        <div class="question-content">
            ${question.content}
        </div>
    `;

    const typeId = parseInt(question.typeId);

    // 문제 유형별 입력 영역
    switch(typeId) {
        case 1: // 객관식
            content += renderMultipleChoice(question);
            break;
        case 2: // 주관식
            content += renderShortAnswer();
            break;
        case 3: // 서술형
            content += renderLongAnswer();
            break;
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

    container.innerHTML = content;
}

// 객관식 문제 렌더링
function renderMultipleChoice(question) {
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

// 정답 확인
function checkAnswer(questionId) {
    const question = questionsDetailMap.get(questionId);
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

        // 맵에서도 삭제
        questionsDetailMap.delete(questionId);
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