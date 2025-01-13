document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const sidebar = document.getElementById('sidebar');

    sidebarToggle.addEventListener('click', () => {
        sidebar.classList.toggle('active');
    });

    // 라우팅 처리
    function handleRoute() {
        const hash = window.location.hash.slice(1) || 'dashboard';
        loadContent(hash);
        updateActiveMenu(hash);
    }

    // 컨텐츠 로드
    async function loadContent(page) {
        const contentDiv = document.getElementById('content');

        try {
            // 로딩 표시
            contentDiv.innerHTML = '<div class="loading">Loading...</div>';

            const response = await fetch(`/api/admin/${page}`);

            if (!response.ok) {
                throw new Error('API 요청 실패');
            }

            const data = await response.json();
            contentDiv.innerHTML = renderContent(page, data);

        } catch (error) {
            console.error('Error:', error);
            contentDiv.innerHTML = '<div class="error">컨텐츠를 불러오는데 실패했습니다.</div>';
        }
    }

    // 컨텐츠 렌더링
    function renderContent(page, data) {
        switch(page) {
            case 'dashboard':
                return renderDashboard(data);
            case 'users':
                return renderUsers(data);
            default:
                return '<div class="error">잘못된 페이지입니다.</div>';
        }
    }

    // 메뉴 활성화 상태 업데이트
    function updateActiveMenu(page) {
        document.querySelectorAll('.sidebar-menu a').forEach(link => {
            link.classList.remove('active');
        });

        const activeLink = document.querySelector(`.sidebar-menu [data-page="${page}"] a`);
        if (activeLink) {
            activeLink.classList.add('active');
        }
    }

    // 모바일 사이드바 처리
    if (window.innerWidth <= 768) {
        document.querySelectorAll('.sidebar-menu a').forEach(item => {
            item.addEventListener('click', () => {
                sidebar.classList.remove('active');
            });
        });
    }

    // 초기 라우트 처리
    handleRoute();

    // 해시 변경 감지
    window.addEventListener('hashchange', handleRoute);
});

// 대시보드 렌더링
function renderDashboard(data) {
    return `
        <div class="dashboard">
            <h2>대시보드</h2>
            <!-- 대시보드 컨텐츠 -->
        </div>
    `;
}

// 사용자 관리 렌더링
function renderUsers(data) {
    return `
        <div class="users">
            <h2>사용자 관리</h2>
            <!-- 사용자 목록 -->
        </div>
    `;
}