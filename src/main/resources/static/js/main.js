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