// 프로필 페이지 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 상태 관리
    let currentContent = null;
    let isLoading = false;

    // 메뉴 클릭 이벤트 리스너 설정
    setupNavigation();

    // URL에서 초기 컨텐츠 확인 및 로드
    loadInitialContent();

    function setupNavigation() {
        const navItems = document.querySelectorAll('.nav-item[data-content]');

        navItems.forEach(item => {
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
    }

    function updateActiveMenu(contentType) {
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

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

            const html = await response.text();
            contentArea.innerHTML = html;

            currentContent = contentType;
            updateURL(contentType);
            executeContentScripts(contentArea);

        } catch (error) {
            showError(contentArea, error.message);
        } finally {
            isLoading = false;
        }
    }

    function loadInitialContent() {
        const path = window.location.pathname;
        const match = path.match(/\/user\/profile\/([^\/]+)/);
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

    function executeContentScripts(container) {
        container.querySelectorAll('script').forEach(script => {
            const newScript = document.createElement('script');
            Array.from(script.attributes).forEach(attr => {
                newScript.setAttribute(attr.name, attr.value);
            });
            newScript.textContent = script.textContent;
            script.parentNode.replaceChild(newScript, script);
        });
    }

    window.addEventListener('popstate', (event) => {
        if (event.state && event.state.contentType) {
            updateActiveMenu(event.state.contentType);
            loadContent(event.state.contentType);
        }
    });
});

// 회원 탈퇴 모달 관련 함수들
function confirmDelete() {
    document.getElementById('deactivateModal').style.display = 'block';
}

function closeDeactivateModal() {
    document.getElementById('deactivateModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('deactivateModal');
    if (event.target == modal) {
        closeDeactivateModal();
    }
}