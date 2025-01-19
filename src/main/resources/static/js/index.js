document.addEventListener('DOMContentLoaded', function () {
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

function checkPasswordStrength(password) {
    const strengthBar = document.getElementById('strengthBar');
    const hint = document.getElementById('passwordHint');

    // 비밀번호 강도 검사
    const hasLetter = /[A-Za-z]/.test(password);
    const hasDigit = /\d/.test(password);
    const hasSpecial = /[@$!%*#?&]/.test(password);
    const isLongEnough = password.length >= 8;

    let strength = 0;
    if (hasLetter) strength++;
    if (hasDigit) strength++;
    if (hasSpecial) strength++;
    if (isLongEnough) strength++;

    // 강도에 따른 스타일 적용
    strengthBar.className = 'password-strength-bar';
    if (strength < 2) {
        strengthBar.classList.add('strength-weak');
    } else if (strength < 4) {
        strengthBar.classList.add('strength-medium');
    } else {
        strengthBar.classList.add('strength-strong');
    }

    // 힌트 표시
    hint.style.display = (strength < 4) ? 'block' : 'none';
}

function validateForm() {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const confirmHint = document.getElementById('confirmHint');

    if (newPassword !== confirmPassword) {
        confirmHint.style.display = 'block';
        return false;
    }

    confirmHint.style.display = 'none';
    return true;
}