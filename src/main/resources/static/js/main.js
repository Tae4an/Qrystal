document.addEventListener('DOMContentLoaded', function () {
    // 비밀번호 확인 기능
    const signupForm = document.querySelector('form');
    if (signupForm) {
        signupForm.addEventListener('submit', function (e) {
            const password = document.querySelector('input[name="password"]');
            const passwordConfirm = document.querySelector('input[name="passwordConfirm"]');

            if (passwordConfirm && password.value !== passwordConfirm.value) {
                e.preventDefault();
                alert('비밀번호가 일치하지 않습니다.');
            }
        });
    }

    // 스크롤 애니메이션
    const observerOptions = {
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate');
            }
        });
    }, observerOptions);

    const featureItems = document.querySelectorAll('.feature-item');
    if (featureItems.length > 0) {
        featureItems.forEach(item => {
            observer.observe(item);
        });
    }

    // 메인 배너 마우스 효과
    const banner = document.querySelector('.main-banner');
    if (banner) {
        banner.addEventListener('mousemove', (e) => {
            const {clientX, clientY} = e;
            const {width, height} = banner.getBoundingClientRect();
            const x = (clientX / width - 0.5) * 20;
            const y = (clientY / height - 0.5) * 20;

            banner.style.setProperty('--x', `${x}px`);
            banner.style.setProperty('--y', `${y}px`);
        });
    }

    console.log('Page loaded successfully');
});
document.addEventListener('DOMContentLoaded', function() {
    let emailVerified = false;
    let timer = null;
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

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

    // 모든 입력 필드의 변경 감지
    ['email', 'password', 'passwordConfirm', 'name'].forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.addEventListener('input', checkFormValidity);
        }
    });

    async function handleEmailVerification() {
        const email = document.getElementById('email').value;
        const emailStatus = document.getElementById('emailStatus');

        if (!email) {
            emailStatus.textContent = '이메일을 입력해주세요.';
            emailStatus.className = 'status-message error';
            return;
        }

        if (!emailRegex.test(email)) {
            emailStatus.textContent = '올바른 이메일 형식이 아닙니다.';
            emailStatus.className = 'status-message error';
            return;
        }

        try {
            const response = await fetch('/auth/send-verification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email })
            });

            const data = await response.json();

            if (data.success) {
                document.getElementById('verificationArea').style.display = 'block';
                startTimer();
                emailStatus.textContent = '인증코드가 발송되었습니다.';
                emailStatus.className = 'status-message success';
            } else {
                emailStatus.textContent = data.message;
                emailStatus.className = 'status-message error';
            }
        } catch (error) {
            emailStatus.textContent = '인증코드 발송에 실패했습니다.';
            emailStatus.className = 'status-message error';
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
                emailVerified = true;
                clearInterval(timer);
                document.getElementById('email').disabled = true;
                document.getElementById('verifyEmailBtn').disabled = true;
                document.getElementById('verificationArea').style.display = 'none';
                verificationStatus.textContent = '이메일 인증이 완료되었습니다.';
                verificationStatus.className = 'status-message success';
                checkFormValidity();
            } else {
                verificationStatus.textContent = '잘못된 인증코드입니다.';
                verificationStatus.className = 'status-message error';
            }
        } catch (error) {
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

        checkFormValidity();
    }

    function checkFormValidity() {
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const passwordConfirm = document.getElementById('passwordConfirm').value;
        const name = document.getElementById('name').value;
        const signupBtn = document.getElementById('signupBtn');

        const isValid = emailVerified &&
            password &&
            passwordConfirm &&
            password === passwordConfirm &&
            name;

        signupBtn.disabled = !isValid;
    }
});