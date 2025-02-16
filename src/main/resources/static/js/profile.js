document.addEventListener('DOMContentLoaded', function() {
    // 전역 변수
    let currentContent = null;
    let isLoading = false;

    // 초기 컨텐츠 로드
    loadInitialContent();

    // 네비게이션 클릭 이벤트 설정
    document.querySelectorAll('.nav-item[data-content]').forEach(item => {
        item.addEventListener('click', async (e) => {
            e.preventDefault();
            const contentType = item.dataset.content;

            // 현재 활성화된 메뉴와 같은 경우 무시
            if (currentContent === contentType) return;

            // 메뉴 활성화 상태 변경
            updateActiveMenu(contentType);

            // 컨텐츠 로드
            await loadContent(contentType);
        });
    });

    function updateActiveMenu(contentType) {
        // 모든 메뉴 비활성화
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // 선택된 메뉴 활성화
        const activeItem = document.querySelector(`.nav-item[data-content="${contentType}"]`);
        if (activeItem) {
            activeItem.classList.add('active');
        }
    }

    async function loadContent(contentType) {
        if (isLoading) return;

        const contentArea = document.getElementById('profileContent');

        try {
            isLoading = true;
            showLoading(contentArea);

            const response = await fetch(`/user/profile/${contentType}`, {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error('컨텐츠를 불러오는데 실패했습니다.');
            }

            contentArea.innerHTML = await response.text();

            // statistics 페이지인 경우
            if (contentType === 'statistics') {
                // statistics.js 로드
                const script = document.createElement('script');
                script.src = '/js/statistics.js';
                script.onload = function() {
                    // 스크립트 로드 완료 후 초기화 함수 실행
                    if (typeof window.initializeStatistics === 'function') {
                        window.initializeStatistics();
                    }
                };
                document.body.appendChild(script);
            }

            currentContent = contentType;

        } catch (error) {
            showError(contentArea, error.message);
        } finally {
            isLoading = false;
        }
    }

    function loadInitialContent() {
        // URL에서 컨텐츠 타입 추출
        const path = window.location.pathname;
        const match = path.match(/\/user\/profile\/([^\/]+)/);

        // 기본값은 'my-questions'
        const contentType = match ? match[1] : 'my-questions';

        updateActiveMenu(contentType);
        loadContent(contentType);
    }

    function updateURL(contentType) {
        const newUrl = `/user/profile/${contentType}`;
        window.history.pushState({ contentType }, '', newUrl);
    }

    function showLoading(container) {
        container.innerHTML = `
            <div class="loading">
                <div class="loading-spinner"></div>
            </div>
        `;
    }

    function showError(container, message) {
        container.innerHTML = `
            <div class="error-message">
                <i class="fas fa-exclamation-circle"></i>
                <p>${message}</p>
                <button class="btn btn-primary" onclick="window.location.reload()">
                    <i class="fas fa-redo"></i> 다시 시도
                </button>
            </div>
        `;
    }

    // 브라우저 뒤로가기/앞으로가기 처리
    window.addEventListener('popstate', (event) => {
        if (event.state && event.state.contentType) {
            updateActiveMenu(event.state.contentType);
            loadContent(event.state.contentType);
        }
    });
});

// 회원 탈퇴 모달 관련 함수
function confirmDelete() {
    document.getElementById('deactivateModal').style.display = 'block';
}

function closeDeactivateModal() {
    document.getElementById('deactivateModal').style.display = 'none';
}

// 모달 외부 클릭시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('deactivateModal');
    if (event.target == modal) {
        closeDeactivateModal();
    }
}