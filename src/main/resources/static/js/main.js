document.addEventListener('DOMContentLoaded', function() {
    // 비밀번호 확인 기능
    const signupForm = document.querySelector('form');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
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
            const { clientX, clientY } = e;
            const { width, height } = banner.getBoundingClientRect();
            const x = (clientX / width - 0.5) * 20;
            const y = (clientY / height - 0.5) * 20;

            banner.style.setProperty('--x', `${x}px`);
            banner.style.setProperty('--y', `${y}px`);
        });
    }

    console.log('Page loaded successfully');
});