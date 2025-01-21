// form.js
document.addEventListener('DOMContentLoaded', function() {
    initializeEditors();
    loadCategories();
    setupEventListeners();

    // 수정 모드인 경우 데이터 로드
    const questionId = document.getElementById('questionId').value;
    if (questionId) {
        loadQuestionData(questionId);
        document.getElementById('formTitle').textContent = '문제 수정';
    }
});

// 전역 변수
let editor, explanationEditor;
let tags = new Set();

// 에디터 초기화
function initializeEditors() {
    // 문제 내용 에디터
    editor = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: [
                [{ 'header': [1, 2, 3, false] }],
                ['bold', 'italic', 'underline'],
                ['image', 'code-block'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                [{ 'align': [] }],
                ['clean']
            ]
        },
        placeholder: '문제 내용을 입력하세요...'
    });

    // 해설 에디터
    explanationEditor = new Quill('#explanationEditor', {
        theme: 'snow',
        modules: {
            toolbar: [
                [{ 'header': [1, 2, 3, false] }],
                ['bold', 'italic', 'underline'],
                ['image', 'code-block'],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                [{ 'align': [] }],
                ['clean']
            ]
        },
        placeholder: '문제 해설을 입력하세요...'
    });
}

let currentQuestionData = null;

// 카테고리 로드
async function loadCategories() {
    try {
        const response = await fetch('/api/categories');
        if (!response.ok) throw new Error('카테고리 로드 실패');
        const categories = await response.json();
        renderCategoryOptions(categories);

        // 수정 모드일 경우 카테고리 설정
        if (currentQuestionData) {
            document.getElementById('categoryId').value = currentQuestionData.categoryId;
        }
    } catch (error) {
        console.error('카테고리 로드 실패:', error);
        showToast('카테고리 목록을 불러오는데 실패했습니다.', 'error');
    }
}


// 카테고리 옵션 렌더링
function renderCategoryOptions(categories, parentElement = document.getElementById('categoryId'), level = 0) {
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.id;
        option.textContent = '　'.repeat(level) + category.name;
        parentElement.appendChild(option);

        if (category.children && category.children.length > 0) {
            renderCategoryOptions(category.children, parentElement, level + 1);
        }
    });
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // 문제 유형 변경 시
    document.getElementById('typeId').addEventListener('change', function(e) {
        const choicesSection = document.getElementById('choicesSection');
        if (e.target.value === '1') { // 객관식
            choicesSection.style.display = 'block';
            if (!document.querySelector('.choice-item')) {
                // 객관식 선택 시 기본 2개 보기 추가
                addChoice();
                addChoice();
            }
            updateAnswerInput();
        } else {
            choicesSection.style.display = 'none';
            choicesSection.style.display = 'none';
            updateAnswerInput();
        }
    });

    // 태그 입력
    const tagInput = document.getElementById('tagInput');
    tagInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const value = this.value.trim();
            if (value && !tags.has(value)) {
                tags.add(value);
                renderTags();
            }
            this.value = '';
        }
    });

    // 난이도 선택 초기화 및 이벤트 설정
    initializeDifficultyStars();
}

function initializeDifficultyStars() {
    const difficultyContainer = document.querySelector('.difficulty-select');
    const labels = difficultyContainer.querySelectorAll('label');
    const inputs = difficultyContainer.querySelectorAll('input[type="radio"]');

    // 모든 별을 회색으로 초기화
    labels.forEach(label => {
        label.style.color = '#d1d5db';
    });

    // 각 별에 마우스 이벤트 추가
    labels.forEach((label, index) => {
        label.addEventListener('mouseenter', () => {
            // 현재 별까지 색상 변경
            for (let i = 0; i <= index; i++) {
                labels[i].style.color = '#f59e0b';
            }
        });

        label.addEventListener('mouseleave', () => {
            // 선택된 별이 없으면 모두 회색으로
            const checkedInput = difficultyContainer.querySelector('input[type="radio"]:checked');
            if (!checkedInput) {
                labels.forEach(l => l.style.color = '#d1d5db');
            } else {
                // 선택된 별까지만 색상 유지
                const checkedIndex = Array.from(inputs).indexOf(checkedInput);
                labels.forEach((l, i) => {
                    l.style.color = i <= checkedIndex ? '#f59e0b' : '#d1d5db';
                });
            }
        });
    });

    // 클릭 이벤트
    inputs.forEach((input, index) => {
        input.addEventListener('change', () => {
            labels.forEach((l, i) => {
                l.style.color = i <= index ? '#f59e0b' : '#d1d5db';
            });
        });
    });
}

// form submit 이벤트 리스너
document.getElementById('questionForm').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작 방지
});

// 저장 전 검증과 저장을 처리하는 함수
function validateAndSave(event) {
    // 기본 form submit 동작 방지
    if (event) {
        event.preventDefault();
    }

    if (validateQuestionForm()) {
        saveQuestion();
    }
}

// 필수 입력값 검증
function validateQuestionForm() {
    console.log('Validating question form...');  // 디버깅용 로그 추가

    // 카테고리 검증
    const categoryId = document.getElementById('categoryId');
    if (!categoryId || !categoryId.value) {
        alert('카테고리를 선택해주세요.');
        if (categoryId) categoryId.focus();
        return false;
    }
    // 문제 유형 검증
    const typeId = document.getElementById('typeId').value;
    if (!typeId) {
        alert('문제 유형을 선택해주세요.');
        document.getElementById('typeId').focus();
        return false;
    }

    // 제목 검증
    const title = document.getElementById('title').value.trim();
    if (!title) {
        alert('제목을 입력해주세요.');
        document.getElementById('title').focus();
        return false;
    }

    // 문제 내용 검증
    const content = editor.root.innerHTML.trim();
    if (!content || content === '<p><br></p>') {
        alert('문제 내용을 입력해주세요.');
        editor.focus();
        return false;
    }

    // 객관식인 경우 보기 검증
    if (typeId === '1') {
        const choices = document.querySelectorAll('.choice-item input');
        if (choices.length < 2) {
            alert('객관식 문제는 최소 2개의 보기가 필요합니다.');
            return false;
        }

        // 보기 내용 검증
        for (let i = 0; i < choices.length; i++) {
            if (!choices[i].value.trim()) {
                alert(`${i + 1}번 보기 내용을 입력해주세요.`);
                choices[i].focus();
                return false;
            }
        }

        // 정답 번호 검증
        const answer = document.getElementById('answer').value;
        if (!answer || parseInt(answer) < 1 || parseInt(answer) > choices.length) {
            alert('올바른 정답 번호를 선택해주세요.');
            document.getElementById('answer').focus();
            return false;
        }
    } else {
        // 주관식/서술형 정답 검증
        const answer = document.getElementById('answer').value.trim();
        if (!answer) {
            alert('정답을 입력해주세요.');
            document.getElementById('answer').focus();
            return false;
        }
    }

    // 난이도 검증
    const difficulty = document.querySelector('input[name="difficulty"]:checked');
    if (!difficulty) {
        alert('난이도를 선택해주세요.');
        return false;
    }

    return true;
}
// 객관식 보기 추가
function addChoice() {
    const choicesList = document.getElementById('choicesList');
    if (choicesList.children.length >= 5) {
        alert('객관식 보기는 최대 5개까지만 가능합니다.');
        return;
    }

    const choiceNumber = choicesList.children.length + 1;
    const choiceItem = document.createElement('div');
    choiceItem.className = 'choice-item';
    choiceItem.innerHTML = `
        <div class="choice-number">${choiceNumber}</div>
        <input type="text" class="form-control" placeholder="보기 내용" required>
        <button type="button" class="btn btn-outline" onclick="removeChoice(this)" 
            ${choicesList.children.length <= 2 ? 'disabled' : ''}>
            <i class="fas fa-times"></i>
        </button>
    `;

    choicesList.appendChild(choiceItem);
    updateAnswerInput();
}

// 객관식 보기 제거
function removeChoice(button) {
    const choicesList = document.getElementById('choicesList');
    if (choicesList.children.length <= 2) {
        alert('객관식 문제는 최소 2개의 보기가 필요합니다.');
        return;
    }

    button.closest('.choice-item').remove();

    // 번호 재정렬
    Array.from(choicesList.children).forEach((item, index) => {
        item.querySelector('.choice-number').textContent = index + 1;
    });
    updateAnswerInput();
}
// 문제 유형에 따른 정답 입력 UI 변경
function updateAnswerInput() {
    const typeId = document.getElementById('typeId').value;
    const answerContainer = document.getElementById('answerContainer');

    if (typeId === '1') { // 객관식
        const choicesCount = document.getElementById('choicesList').children.length;
        answerContainer.innerHTML = `
            <label for="answer">정답</label>
            <select id="answer" class="form-control" required>
                <option value="">정답 선택</option>
                ${Array.from({length: choicesCount}, (_, i) =>
            `<option value="${i + 1}">${i + 1}번</option>`
        ).join('')}
            </select>
        `;
    } else {
        answerContainer.innerHTML = `
            <label for="answer">정답</label>
            <input type="text" id="answer" class="form-control" required>
        `;
    }
}
// 태그 렌더링
function renderTags() {
    const tagsList = document.getElementById('tagsList');
    tagsList.innerHTML = '';

    tags.forEach(tag => {
        const tagElement = document.createElement('div');
        tagElement.className = 'tag-item';
        tagElement.innerHTML = `
           <span>${tag}</span>
           <span class="tag-remove" onclick="removeTag('${tag}')">×</span>
       `;
        tagsList.appendChild(tagElement);
    });
}

// 태그 제거
function removeTag(tag) {
    tags.delete(tag);
    renderTags();
}

// 문제 저장
async function saveQuestion() {
    try {
        const questionId = document.getElementById('questionId').value;
        const formData = {
            categoryId: document.getElementById('categoryId').value,
            typeId: document.getElementById('typeId').value,
            title: document.getElementById('title').value,
            content: editor.root.innerHTML,
            answer: document.getElementById('answer').value,
            explanation: explanationEditor.root.innerHTML,
            difficulty: document.querySelector('input[name="difficulty"]:checked').value,
            isPublic: document.getElementById('isPublic').checked,
            tags: Array.from(tags)
        };

        // 객관식인 경우 보기 추가
        if (formData.typeId === '1') {
            formData.choices = Array.from(document.querySelectorAll('.choice-item input')).map((input, index) => ({
                choiceNumber: index + 1,
                content: input.value
            }));
        }

        console.log('전송할 데이터:', formData);  // 데이터 확인용 로그

        const url = questionId ?
            `/api/questions/${questionId}` :
            '/api/questions';

        const response = await fetch(url, {
            method: questionId ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        console.log('응답 상태:', response.status);  // 응답 상태 코드 확인

        if (!response.ok) {
            const errorData = await response.json();
            console.error('서버 응답 에러:', errorData);  // 서버 에러 메시지 확인
            throw new Error(errorData.message || '문제 저장 실패');
        }

        showToast(questionId ? '문제가 수정되었습니다.' : '문제가 저장되었습니다.');
        setTimeout(() => {
            location.href = '/questions';
        }, 1500);
    } catch (error) {
        console.error('문제 저장 실패 상세:', error);  // 상세 에러 로그
        showToast('문제 저장에 실패했습니다.', 'error');
    }
}
// 문제 데이터 로드 (수정 시)
async function loadQuestionData(questionId) {
    try {
        const response = await fetch(`/api/questions/${questionId}`);
        if (!response.ok) throw new Error('문제 로드 실패');
        const question = await response.json();

        currentQuestionData = question;

        // 카테고리 옵션이 이미 로드되어 있다면 바로 설정
        if (document.getElementById('categoryId').options.length > 1) {
            document.getElementById('categoryId').value = question.categoryId;
        }
        console.log("로드된 문제 데이터:", question);

        // 기본 정보 설정
        document.getElementById('categoryId').value = question.categoryId;
        document.getElementById('title').value = question.title;
        editor.root.innerHTML = question.content;
        document.getElementById('answer').value = question.answer;
        explanationEditor.root.innerHTML = question.explanation || '';
        document.querySelector(`input[name="difficulty"][value="${question.difficulty}"]`).checked = true;
        document.getElementById('isPublic').checked = question.isPublic;

        // 문제 유형 설정 및 객관식 보기 처리
        const typeSelect = document.getElementById('typeId');
        typeSelect.value = question.typeId;

        // 객관식인 경우 보기 추가
        if (question.typeId === 1 && question.choices) {
            const choicesSection = document.getElementById('choicesSection');
            const choicesList = document.getElementById('choicesList');

            choicesSection.style.display = 'block';
            choicesList.innerHTML = '';  // 기존 보기 초기화

            question.choices.forEach(choice => {
                const choiceItem = document.createElement('div');
                choiceItem.className = 'choice-item';
                choiceItem.innerHTML = `
                   <div class="choice-number">${choice.choiceNumber}</div>
                   <input type="text" class="form-control" value="${choice.content}" required>
                   <button type="button" class="btn btn-outline" onclick="removeChoice(this)">
                       <i class="fas fa-times"></i>
                   </button>
               `;
                choicesList.appendChild(choiceItem);
            });
        }

        // 태그 설정
        if (question.tags) {
            tags = new Set(question.tags);
            renderTags();
        }
    } catch (error) {
        console.error('문제 로드 실패:', error);
        showToast('문제 정보를 불러오는데 실패했습니다.', 'error');
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