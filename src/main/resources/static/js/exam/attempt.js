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
    initializeChoiceItems();

    // 객관식 문항 초기화
    function initializeChoiceItems() {
        document.querySelectorAll('.choice-item').forEach(item => {
            item.addEventListener('click', function(e) {
                const radio = this.querySelector('input[type="radio"]');
                if (radio && e.target !== radio) {
                    radio.checked = !radio.checked;
                    radio.dispatchEvent(new Event('change'));
                }
                updateChoiceItemStyles();
            });
        });
    }

    // 객관식 선택 스타일 업데이트
    function updateChoiceItemStyles() {
        document.querySelectorAll('.choice-item').forEach(item => {
            const radio = item.querySelector('input[type="radio"]');
            item.classList.toggle('selected', radio.checked);
        });
    }

    // 타이머 초기화 및 시작
    function initializeTimer() {
        updateTimerDisplay();
        timer = setInterval(() => {
            remainingSeconds--;
            updateTimerDisplay();

            if (remainingSeconds <= 300) { // 5분 이하
                timerDisplay.classList.add('warning');
            }

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
            questionTypeId: parseInt(data.questionType),
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
        // 현재 문제 페이드 아웃
        const currentQuestion = document.querySelector('.question-item.active');
        if (currentQuestion) {
            currentQuestion.style.opacity = '0';
            currentQuestion.style.transform = 'translateX(20px)';
        }

        setTimeout(() => {
            questions.forEach((q, i) => {
                q.classList.toggle('active', i === index);
            });
            navItems.forEach((item, i) => {
                item.classList.toggle('active', i === index);
            });

            // 새 문제 페이드 인
            const newQuestion = questions[index];
            if (newQuestion) {
                newQuestion.style.opacity = '1';
                newQuestion.style.transform = 'translateX(0)';
            }

            updateQuestionNavigation();
        }, 300);
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
            if (hasAnswer) {
                statusIcon.classList.add('visible');
            } else {
                statusIcon.classList.remove('visible');
            }
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

        try {
            const response = await fetch(`/api/exams/${examId}/attempts/${attemptId}/submit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (response.ok) {
                window.location.href = `/exams/${examId}/result/${attemptId}`;
            } else {
                console.error('Submit error:', result);
                alert(result.message || '제출 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Submit error details:', error);
            alert('제출 중 오류가 발생했습니다.');
        }
    }

    // 자동 제출
    function autoSubmitExam() {
        alert('시험 시간이 종료되었습니다. 자동으로 제출됩니다.');
        submitExam();
    }

    // 답안 체크 및 모달 표시
    function checkAndShowModal() {
        const unansweredQuestions = getUnansweredQuestions();

        if (unansweredQuestions.length > 0) {
            // 미답변 문항이 있는 경우
            const firstUnanswered = unansweredQuestions[0];
            const questionIndex = Array.from(questions).findIndex(q =>
                q.getAttribute('data-question-id') === firstUnanswered.questionId);

            // 해당 문항으로 이동
            currentQuestionIndex = questionIndex;
            showQuestion(currentQuestionIndex);

            // 미답변 문항 표시
            highlightUnansweredQuestions(unansweredQuestions);

            alert(`답변하지 않은 문항이 ${unansweredQuestions.length}개 있습니다. 모든 문항에 답변해주세요.`);
            return;
        }

        // 모달 표시

        submitModal.style.display = 'block';
        setTimeout(() => {
            submitModal.classList.add('show');
        }, 10);
    }

    // 미답변 문항 확인
    function getUnansweredQuestions() {
        const unanswered = [];
        questions.forEach(question => {
            const questionId = question.getAttribute('data-question-id');
            const questionType = question.getAttribute('data-question-type');

            let hasAnswer = false;
            if (questionType === '1') { // 객관식
                hasAnswer = question.querySelector('input[type="radio"]:checked') !== null;
            } else { // 주관식, 서술형
                const input = question.querySelector('.answer-input');
                hasAnswer = input && input.value.trim() !== '';
            }

            if (!hasAnswer) {
                const questionNumber = question.querySelector('.question-number').textContent.trim();
                const points = question.querySelector('.question-points').textContent.trim();
                unanswered.push({ questionId, questionNumber, points });
            }
        });
        return unanswered;
    }

    // 미답변 문항 하이라이트
    function highlightUnansweredQuestions(unansweredQuestions) {
        // 모든 문항의 하이라이트 제거
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('unanswered');
        });

        // 미답변 문항 하이라이트
        unansweredQuestions.forEach(q => {
            const navItem = document.querySelector(`.nav-item[data-question-id="${q.questionId}"]`);
            if (navItem) {
                navItem.classList.add('unanswered');
            }
        });

        // 3초 후 하이라이트 제거
        setTimeout(() => {
            document.querySelectorAll('.nav-item').forEach(item => {
                item.classList.remove('unanswered');
            });
        }, 3000);
    }

    // 모달 숨기기
    function hideModal() {
        submitModal.classList.remove('show');
        setTimeout(() => {
            submitModal.style.display = 'none';
        }, 300);
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

    submitButton.addEventListener('click', checkAndShowModal);
    confirmSubmit.addEventListener('click', submitExam);
    cancelSubmit.addEventListener('click', hideModal);

    // 답안 입력 감지
    document.querySelectorAll('.answer-input, input[type="radio"]').forEach(input => {
        input.addEventListener('change', (e) => {
            const questionItem = e.target.closest('.question-item');
            const questionId = questionItem.dataset.questionId;
            updateAnswerStatus(questionId, true);
            if (e.target.type === 'radio') {
                updateChoiceItemStyles();
            }
        });
    });

    // 문제 번호 클릭 시 해당 문제로 이동
    navItems.forEach((item, index) => {
        item.addEventListener('click', () => {
            currentQuestionIndex = index;
            showQuestion(currentQuestionIndex);
        });
    });

    // 새로고침 감지 및 처리
    if (performance.navigation.type === 1) {
        navigator.sendBeacon(`/api/exams/${examId}/attempts/${attemptId}/cancel`);
        window.location.href = '/exams';
    }

    // 페이지 이탈 처리
    window.addEventListener('beforeunload', (e) => {
        e.preventDefault();
        e.returnValue = '시험을 종료하시겠습니까? 현재까지의 진행사항이 모두 취소됩니다.';
    });

    window.addEventListener('unload', () => {
        navigator.sendBeacon(`/api/exams/${examId}/attempts/${attemptId}/cancel`);
    });
});