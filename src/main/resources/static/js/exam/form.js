// 선택된 문제들을 저장할 배열
let selectedQuestions = [];
let isPublic = true;  // 공개 여부 기본값
let initialFormState = null;

document.addEventListener('DOMContentLoaded', async function() {
    await initializeForm();
    await loadCategories();
    await loadQuestions();
});

async function initializeForm() {
    const examForm = document.getElementById('examForm');
    if (examForm) {
        examForm.addEventListener('submit', handleSubmit);
    }

    const examId = document.getElementById('examId')?.value;
    if (examId) {
        await loadCategories();
        await loadExamData(examId);
    }
}

// 공개 여부 토글
function toggleVisibilityOptions() {
    const options = document.getElementById('visibilityOptions');
    options.style.display = options.style.display === 'none' ? 'block' : 'none';
}

// 공개 여부 선택
function selectVisibility(visibility) {
    isPublic = visibility;
    const btn = document.getElementById('visibilityBtn');
    btn.innerHTML = visibility ?
        '공개 <i class="fas fa-chevron-down"></i>' :
        '비공개 <i class="fas fa-chevron-down"></i>';
    toggleVisibilityOptions();
}

// 카테고리 로드
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');

        const categories = await response.json();
        const categorySelect = document.getElementById('categoryId');

        function renderCategoryOptions(categories, level = 0) {
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = '　'.repeat(level) + category.name;
                categorySelect.appendChild(option);

                if (category.children && category.children.length > 0) {
                    renderCategoryOptions(category.children, level + 1);
                }
            });
        }

        renderCategoryOptions(categories);
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}

// 문제 목록 로드
async function loadQuestions() {
    try {
        const myQuestionsResponse = await fetch('/api/questions/my', {
            credentials: 'same-origin'
        });
        if (!myQuestionsResponse.ok) throw new Error('내 문제 로드 실패');
        const myQuestions = await myQuestionsResponse.json();
        renderQuestionList(myQuestions, 'myQuestionsList');

        const publicQuestionsResponse = await fetch('/api/questions?status=ACTIVE&isPublic=true', {
            credentials: 'same-origin'
        });
        if (!publicQuestionsResponse.ok) throw new Error('공개 문제 로드 실패');
        const publicQuestions = await publicQuestionsResponse.json();
        renderQuestionList(publicQuestions, 'publicQuestionsList');

    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 목록을 불러오는데 실패했습니다.');
    }
}

// 문제 목록 렌더링
function renderQuestionList(questions, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (questions.length === 0) {
        container.innerHTML = '<div class="no-questions">문제가 없습니다.</div>';
        return;
    }

    container.innerHTML = questions.map(question => `
       <div class="question-item">
           <div class="question-info">
               <h4>${question.title}</h4>
               <div class="question-meta">
                   <span class="question-type">${question.typeName || getQuestionType(question.typeId)}</span>
                   <span class="question-difficulty">난이도: ${question.difficulty}</span>
                   <span class="category-path">${question.categoryName || '미분류'}</span>
               </div>
           </div>
           <button type="button" class="btn btn-sm btn-primary"
                   onclick="addQuestion(${question.id}, '${question.title.replace(/'/g, "\\'")}')">
               추가
           </button>
       </div>
   `).join('');
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

// 문제 추가
function addQuestion(questionId, title) {
    if (selectedQuestions.some(q => q.questionId === questionId)) {
        alert('이미 선택된 문제입니다.');
        return;
    }

    const newQuestion = {
        questionId: questionId,
        questionNumber: selectedQuestions.length + 1,
        point: 5,
        title: title
    };
    selectedQuestions.push(newQuestion);
    updateSelectedQuestionsUI();
}

// 선택된 문제 목록 UI 업데이트
function updateSelectedQuestionsUI() {
    const container = document.querySelector('#selectedQuestions .sortable-list');

    container.innerHTML = selectedQuestions.map((question, index) => `
        <li class="sortable-item" data-index="${index}">
            <div class="drag-handle">
                <i class="fas fa-grip-lines"></i>
            </div>
            <div class="question-number">${index + 1}</div>
            <div class="question-title flex-grow">${question.title}</div>
            <input type="number" class="point-input" value="${question.point}"
                   onchange="updatePoint(${index}, this.value)" min="1">
            <button type="button" class="btn btn-sm btn-danger" onclick="removeQuestion(${index})">
                <i class="fas fa-times"></i>
            </button>
        </li>
    `).join('');

    initSortable();
}
function initSortable() {
    const sortableList = document.querySelector('.sortable-list');
    let draggingItem = null;
    let sortableItems = [];

    document.querySelectorAll('.sortable-item').forEach(item => {
        item.addEventListener('mousedown', onMouseDown);
    });

    function onMouseDown(e) {
        if (!e.target.closest('.drag-handle')) return;

        draggingItem = e.target.closest('.sortable-item');
        const rect = draggingItem.getBoundingClientRect();

        // 드래그 시작 시 스타일 적용
        draggingItem.classList.add('dragging');

        // 초기 위치 설정
        sortableItems = [...document.querySelectorAll('.sortable-item:not(.dragging)')];

        document.addEventListener('mousemove', onMouseMove);
        document.addEventListener('mouseup', onMouseUp);
    }

    function onMouseMove(e) {
        if (!draggingItem) return;

        // 마우스 위치에 따라 위치 계산
        const closestItem = sortableItems.reduce((closest, child) => {
            const box = child.getBoundingClientRect();
            const offset = e.clientY - box.top - box.height / 2;

            if (offset < 0 && offset > closest.offset) {
                return { offset, element: child };
            } else {
                return closest;
            }
        }, { offset: Number.NEGATIVE_INFINITY }).element;

        // 실시간으로 위치 변경
        if (closestItem) {
            const index = sortableItems.indexOf(closestItem);
            sortableList.insertBefore(draggingItem, closestItem);
        } else {
            sortableList.appendChild(draggingItem);
        }

        // 번호 업데이트
        updateQuestionNumbers();
    }

    function onMouseUp() {
        if (!draggingItem) return;

        // 드래그 종료 시 스타일 제거
        draggingItem.classList.remove('dragging');

        // 순서 저장
        const newOrder = [...document.querySelectorAll('.sortable-item')].map(item =>
            selectedQuestions[parseInt(item.dataset.index)]
        );
        selectedQuestions = newOrder;

        // 이벤트 리스너 제거
        document.removeEventListener('mousemove', onMouseMove);
        document.removeEventListener('mouseup', onMouseUp);

        draggingItem = null;
    }

    function updateQuestionNumbers() {
        document.querySelectorAll('.sortable-item').forEach((item, index) => {
            const numberElement = item.querySelector('.question-number');
            if (numberElement) {
                numberElement.textContent = index + 1;
            }
        });
    }
}

// 문제 삭제
function removeQuestion(index) {
    selectedQuestions.splice(index, 1);
    selectedQuestions.forEach((q, i) => {
        q.questionNumber = i + 1;
    });
    updateSelectedQuestionsUI();
}

// 배점 수정
function updatePoint(index, value) {
    selectedQuestions[index].point = parseInt(value);
}

// 폼 제출 처리
async function handleSubmit(e) {
    e.preventDefault();

    if (selectedQuestions.length === 0) {
        alert('최소 한 개 이상의 문제를 선택해주세요.');
        return;
    }

    const examId = document.getElementById('examId')?.value;
    const confirmMessage = examId ?
        '모의고사를 수정하시겠습니까?' :
        '모의고사를 생성하시겠습니까?';

    if (!confirm(confirmMessage)) return;

    const formData = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        timeLimit: parseInt(document.getElementById('timeLimit').value),
        categoryId: document.getElementById('categoryId').value,
        isPublic: isPublic,
        status: 'PUBLISHED',
        questions: selectedQuestions
    };

    try {
        const url = examId ? `/api/exams/${examId}` : '/api/exams';
        const method = examId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            throw new Error('서버 오류가 발생했습니다.');
        }

        alert(examId ? '모의고사가 수정되었습니다.' : '모의고사가 생성되었습니다.');
        window.location.href = '/exams/my';

    } catch (error) {
        console.error('Error:', error);
        alert('오류가 발생했습니다: ' + error.message);
    }
}

// 취소 처리
function handleCancel() {
    if (!hasChanges()) {
        window.location.href = '/exams/my';
        return;
    }

    const examId = document.getElementById('examId')?.value;
    const confirmMessage = examId ?
        '수정된 내용이 있습니다. 취소하시면 변경사항이 모두 사라집니다.\n취소하시겠습니까?' :
        '작성 중인 내용이 있습니다. 취소하시면 작성하신 내용이 모두 사라집니다.\n취소하시겠습니까?';

    if (confirm(confirmMessage)) {
        window.location.href = '/exams/my';
    }
}

// 기존 모의고사 데이터 로드
async function loadExamData(examId) {
    try {
        const response = await fetch(`/api/exams/${examId}`);
        if (!response.ok) throw new Error('모의고사 데이터를 불러올 수 없습니다.');

        const exam = await response.json();
        console.log('Loaded exam data:', exam);

        // 기본 정보 설정
        document.getElementById('title').value = exam.title;
        document.getElementById('description').value = exam.description || '';
        document.getElementById('timeLimit').value = exam.timeLimit;
        document.getElementById('categoryId').value = exam.categoryId;

        // 공개/비공개 상태 설정
        isPublic = exam.isPublic;
        const visibilityBtn = document.getElementById('visibilityBtn');
        visibilityBtn.innerHTML = isPublic ?
            '공개 <i class="fas fa-chevron-down"></i>' :
            '비공개 <i class="fas fa-chevron-down"></i>';

        // 선택된 문제 설정
        selectedQuestions = exam.questions.map(q => ({
            questionId: q.questionId,
            questionNumber: q.questionNumber,
            point: q.point,
            title: q.title
        }));

        updateSelectedQuestionsUI();

        // 초기 상태 저장
        initialFormState = getFormState();

    } catch (error) {
        console.error('Error:', error);
        showToast('데이터를 불러오는 중 오류가 발생했습니다.');
    }
}

// 폼의 현재 상태 저장 함수
function getFormState() {
    return {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        timeLimit: document.getElementById('timeLimit').value,
        categoryId: document.getElementById('categoryId').value,
        isPublic: isPublic,
        questions: [...selectedQuestions]  // 배열 복사
    };
}

// 상태 변경 여부 체크 함수
function hasChanges() {
    const examId = document.getElementById('examId')?.value;

    // 새로운 모의고사 생성인 경우
    if (!examId) {
        return document.getElementById('title').value ||
            document.getElementById('description').value ||
            document.getElementById('timeLimit').value ||
            document.getElementById('categoryId').value ||
            selectedQuestions.length > 0;
    }

    // 수정인 경우
    if (!initialFormState) return false;
    const currentState = getFormState();
    return JSON.stringify(currentState) !== JSON.stringify(initialFormState);
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