document.addEventListener('DOMContentLoaded', function() {
    let emailVerified = false;
    let timer = null;
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

    // 이메일 입력 필드 실시간 형식 검증
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            const emailValidation = document.getElementById('emailValidation');
            if (this.value && !emailRegex.test(this.value)) {
                emailValidation.textContent = '유효하지 않은 이메일 형식입니다';
                emailValidation.style.display = 'block';
                this.classList.add('invalid-input');
            } else {
                emailValidation.style.display = 'none';
                this.classList.remove('invalid-input');
            }
        });
    }

    // 비밀번호 입력 필드 실시간 형식 검증
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
            const guideMessage = document.getElementById('passwordGuide');

            if (this.value && !passwordPattern.test(this.value)) {
                guideMessage.textContent = '비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다';
                guideMessage.className = 'guide-message error';
            } else if (this.value) {
                guideMessage.textContent = '사용 가능한 비밀번호입니다';
                guideMessage.className = 'guide-message success';
            }
        });
    }

    // 이메일 인증 버튼 클릭 이벤트
    const verifyEmailBtn = document.getElementById('verifyEmailBtn');
    if (verifyEmailBtn) {
        verifyEmailBtn.addEventListener('click', handleEmailVerification);
    }

    // 인증코드 확인 버튼 클릭 이벤트
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    if (verifyCodeBtn) {
        verifyCodeBtn.addEventListener('click', handleCodeVerification);
    }

    // 비밀번호 확인 실시간 체크
    const passwordConfirmInput = document.getElementById('passwordConfirm');
    if (passwordConfirmInput) {
        passwordConfirmInput.addEventListener('input', handlePasswordConfirm);
    }

    // 회원가입 폼 제출 시 검증
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const fields = [
                { id: 'email', name: '이메일' },
                { id: 'password', name: '비밀번호' },
                { id: 'passwordConfirm', name: '비밀번호 확인' },
                { id: 'name', name: '이름' }
            ];

            let isValid = true;
            let firstEmptyField = null;

            fields.forEach(field => {
                const element = document.getElementById(field.id);
                const validation = document.getElementById(`${field.id}Validation`);

                if (!element.value) {
                    isValid = false;
                    element.classList.add('invalid-input');
                    if (validation) {
                        validation.textContent = `${field.name}은(는) 필수입니다`;
                        validation.style.display = 'block';
                    }
                    if (!firstEmptyField) {
                        firstEmptyField = element;
                    }
                } else {
                    element.classList.remove('invalid-input');
                    if (validation) {
                        validation.style.display = 'none';
                    }
                }
            });

            if (!isValid) {
                firstEmptyField.focus();
                return;
            }

            if (!emailVerified) {
                alert('이메일 인증이 필요합니다');
                return;
            }

            // 모든 검증 통과시 실제 폼 제출
            alert('회원가입이 완료되었습니다.');
            signupForm.submit();
        });
    }

    async function handleEmailVerification() {
        const email = document.getElementById('email').value;
        const emailValidation = document.getElementById('emailValidation');
        const emailStatus = document.getElementById('emailStatus');

        // 기본 이메일 입력 검증
        if (!email) {
            emailInput.classList.add('invalid-input');
            emailValidation.textContent = '이메일은 필수입니다';
            emailValidation.style.display = 'block';
            emailInput.focus();
            return;
        }

        if (!emailRegex.test(email)) {
            emailInput.classList.add('invalid-input');
            emailValidation.textContent = '유효하지 않은 이메일 형식입니다';
            emailValidation.style.display = 'block';
            return;
        }

        try {
            // 1. 이메일 중복 검사
            const duplicateCheck = await fetch('/auth/check-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email })
            });
            const checkResult = await duplicateCheck.json();

            if (!checkResult.success) {
                emailValidation.textContent = checkResult.message;
                emailValidation.style.display = 'block';
                emailInput.classList.add('invalid-input');
                return;
            }

            // 2. 이메일 중복 검사 통과 후 인증 코드 발송
            // UI 먼저 업데이트
            emailInput.classList.remove('invalid-input');
            emailValidation.style.display = 'none';
            alert('인증코드가 발송되었습니다.');
            document.getElementById('verificationArea').style.display = 'block';
            startTimer();
            emailStatus.textContent = '제한시간: 5분';
            emailStatus.className = 'status-message';

            // 인증 코드 발송 요청
            const response = await fetch('/auth/send-verification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email })
            });

            const data = await response.json();

            if (!data.success) {
                emailStatus.textContent = data.message;
                emailStatus.className = 'status-message error';
                document.getElementById('verificationArea').style.display = 'none';
                clearInterval(timer);
            }
        } catch (error) {
            emailStatus.textContent = '처리 중 오류가 발생했습니다.';
            emailStatus.className = 'status-message error';
            document.getElementById('verificationArea').style.display = 'none';
            clearInterval(timer);
        }
    }

    function startTimer() {
        let timeLeft = 300; // 5분
        const timerDisplay = document.getElementById('verificationTimer');

        if (timer) clearInterval(timer);

        timer = setInterval(function() {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            timerDisplay.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;

            if (timeLeft <= 0) {
                clearInterval(timer);
                timerDisplay.textContent = '시간 만료';
                document.getElementById('verifyCodeBtn').disabled = true;
            }
            timeLeft--;
        }, 1000);
    }

    async function handleCodeVerification() {
        const code = document.getElementById('verificationCode').value;
        const email = document.getElementById('email').value;
        const verificationStatus = document.getElementById('verificationStatus');

        try {
            const response = await fetch('/auth/verify-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email,
                    code: code
                })
            });

            const data = await response.json();

            if (data.success) {
                alert('이메일 인증이 완료되었습니다.');
                emailVerified = true;
                clearInterval(timer);
                document.getElementById('email').readOnly = true;
                document.getElementById('verifyEmailBtn').disabled = true;
                document.getElementById('verificationArea').style.display = 'none';
                emailStatus.textContent = '이메일 인증 완료';
                emailStatus.className = 'status-message success';
                verificationStatus.textContent = '';
            } else {
                alert('잘못된 인증코드입니다.');
                verificationStatus.textContent = '잘못된 인증코드입니다.';
                verificationStatus.className = 'status-message error';
            }
        } catch (error) {
            alert('인증에 실패했습니다.');
            verificationStatus.textContent = '인증에 실패했습니다.';
            verificationStatus.className = 'status-message error';
        }
    }

    function handlePasswordConfirm() {
        const password = document.getElementById('password').value;
        const passwordConfirm = this.value;
        const matchMessage = document.getElementById('passwordMatch');

        if (password !== passwordConfirm) {
            matchMessage.textContent = '비밀번호가 일치하지 않습니다.';
            matchMessage.className = 'status-message error';
        } else {
            matchMessage.textContent = '비밀번호가 일치합니다.';
            matchMessage.className = 'status-message success';
        }
    }
});