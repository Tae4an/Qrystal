document.addEventListener('DOMContentLoaded', function() {
    // 전역 변수
    let currentQuestionIndex = 0;
    let answers = new Map(); // 답안 저장용
    let timer = null;
    let remainingSeconds = timeLimit * 60;

    // 요소 참조
    const prevButton = document.getElementById('prevQuestion');
    const nextButton = document.getElementById('nextQuestion');
    const submitButton = document.getElementById('submitExam');
    const questions = document.querySelectorAll('.question-item');
    const navItems = document.querySelectorAll('.nav-item');
    const timerDisplay = document.getElementById('remainingTime');
    const submitModal = document.getElementById('submitModal');
    const confirmSubmit = document.getElementById('confirmSubmit');
    const cancelSubmit = document.getElementById('cancelSubmit');

    // 초기 설정
    initializeTimer();
    initializeAutosave();
    updateQuestionNavigation();

    // 타이머 초기화 및 시작
    function initializeTimer() {
        updateTimerDisplay();
        timer = setInterval(() => {
            remainingSeconds--;
            updateTimerDisplay();

            if (remainingSeconds <= 0) {
                clearInterval(timer);
                autoSubmitExam();
            }
        }, 1000);
    }

    // 타이머 표시 업데이트
    function updateTimerDisplay() {
        const hours = Math.floor(remainingSeconds / 3600);
        const minutes = Math.floor((remainingSeconds % 3600) / 60);
        const seconds = remainingSeconds % 60;

        timerDisplay.textContent =
            `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

        if (remainingSeconds <= 300) { // 5분 이하
            timerDisplay.classList.add('warning');
        }
    }

    // 자동 저장 설정
    function initializeAutosave() {
        setInterval(saveAnswers, 30000); // 30초마다 자동 저장
    }

    // 답안 저장
    function saveAnswers() {
        const currentAnswers = collectAnswers();
        const data = Array.from(currentAnswers.entries()).map(([questionId, data]) => ({
            questionId: parseInt(questionId),
            questionTypeId: parseInt(data.questionType),  // 문제 유형 추가
            submittedAnswer: data.answer.toString()
        }));


        fetch(`/api/exams/${examId}/attempts/${attemptId}/save`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    console.error('상세 서버 에러:', err);
                    throw new Error(err.message);
                });
            }
        }).catch(error => {
            console.error('자동 저장 실패 상세:', error);
        });
    }

    // 현재 답안 수집
    function collectAnswers() {
        const answers = new Map();

        document.querySelectorAll('.question-item').forEach(question => {
            const questionId = question.getAttribute('data-question-id');
            const questionType = question.getAttribute('data-question-type');

            let answer = null;
            if (questionType === '1') { // 객관식
                const selected = question.querySelector('input[type="radio"]:checked');
                answer = selected ? selected.value : null;
            } else { // 주관식, 서술형
                const input = question.querySelector('.answer-input');
                answer = input ? input.value.trim() : null;
            }

            if (answer) {
                answers.set(questionId, {
                    answer: answer,
                    questionType: questionType
                });
            }
        });

        return answers;
    }

    // 문제 이동
    function showQuestion(index) {
        questions.forEach((q, i) => {
            q.classList.toggle('active', i === index);
        });
        navItems.forEach((item, i) => {
            item.classList.toggle('active', i === index);
        });
        updateQuestionNavigation();
    }

    // 이전/다음 버튼 상태 업데이트
    function updateQuestionNavigation() {
        prevButton.disabled = currentQuestionIndex === 0;
        nextButton.disabled = currentQuestionIndex === questions.length - 1;
    }

    // 답안 작성 여부 표시 업데이트
    function updateAnswerStatus(questionId, hasAnswer) {
        const navItem = document.querySelector(`.nav-item[data-question-id="${questionId}"]`);
        if (navItem) {
            const statusIcon = navItem.querySelector('.answer-status');
            statusIcon.style.display = hasAnswer ? 'block' : 'none';
        }
        updateAnsweredCount();
    }

    // 답안 개수 업데이트
    function updateAnsweredCount() {
        const currentAnswers = collectAnswers();
        const answeredCount = document.getElementById('answeredCount');
        answeredCount.textContent = currentAnswers.size;
    }

    // 시험 제출
    async function submitExam() {
        const currentAnswers = collectAnswers();
        const data = Array.from(currentAnswers.entries()).map(([questionId, data]) => ({
            questionId: parseInt(questionId),
            questionTypeId: parseInt(data.questionType),
            submittedAnswer: data.answer.toString()
        }));

        console.log('Submit payload:', data);

        try {
            const response = await fetch(`/api/exams/${examId}/attempts/${attemptId}/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            const result = await response.json(); // 먼저 응답을 JSON으로 파싱
            console.log('Server response:', result); // 서버 응답 로그

            if (response.ok) {
                window.location.href = `/exams/${examId}/result/${attemptId}`;
            } else {
                // 상세 에러 메시지 출력
                console.error('Submit error:', result);
                alert(result.message || '제출 중 오류가 발생했습니다.');
            }
        } catch (error) {
            // 에러 상세 정보 출력
            console.error('Submit error details:', error);
            alert('제출 중 오류가 발생했습니다.');
        }
    }

    // 자동 제출
    function autoSubmitExam() {
        alert('시험 시간이 종료되었습니다. 자동으로 제출됩니다.');
        submitExam();
    }

    // 이벤트 리스너
    prevButton.addEventListener('click', () => {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    });

    nextButton.addEventListener('click', () => {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    });

    submitButton.addEventListener('click', () => {
        submitModal.style.display = 'block';
    });

    confirmSubmit.addEventListener('click', () => {
        submitExam();
    });

    cancelSubmit.addEventListener('click', () => {
        submitModal.style.display = 'none';
    });

    // 답안 입력 감지
    document.querySelectorAll('.answer-input, input[type="radio"]').forEach(input => {
        input.addEventListener('change', (e) => {
            const questionItem = e.target.closest('.question-item');
            const questionId = questionItem.dataset.questionId;
            updateAnswerStatus(questionId, true);
        });
    });

    // 문제 번호 클릭 시 해당 문제로 이동
    navItems.forEach((item, index) => {
        item.addEventListener('click', () => {
            currentQuestionIndex = index;
            showQuestion(currentQuestionIndex);
        });
    });

    // 페이지 이탈 방지
    window.addEventListener('beforeunload', (e) => {
        e.preventDefault();
        e.returnValue = '';
    });
});