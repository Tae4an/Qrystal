document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebar = document.getElementById('sidebar');

    sidebarToggle.addEventListener('click', () => {
        sidebar.classList.toggle('active');
    });

    // 현재 페이지에 해당하는 메뉴 활성화
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-menu a').forEach(link => {
        // 현재 URL과 메뉴의 href가 일치하면 active 클래스 추가
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });

    // 모바일 사이드바 처리
    if (window.innerWidth <= 768) {
        document.querySelectorAll('.sidebar-menu a').forEach(item => {
            item.addEventListener('click', () => {
                sidebar.classList.remove('active');
            });
        });
    }
});