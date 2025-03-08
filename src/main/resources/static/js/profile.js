document.addEventListener('DOMContentLoaded', function() {
    // 전역 변수
    let currentContent = null;
    let isLoading = false;
    let currentTransition = null;

    // 초기 컨텐츠 로드
    loadInitialContent();

    // 네비게이션 클릭 이벤트 설정
    document.querySelectorAll('.nav-item[data-content]').forEach(item => {
        item.addEventListener('click', async (e) => {
            e.preventDefault();
            const contentType = item.dataset.content;

            // 현재 활성화된 메뉴와 같은 경우 무시
            if (currentContent === contentType) return;

            // 트랜지션 중인 경우 무시
            if (isLoading || currentTransition) return;

            // 메뉴 활성화 상태 변경
            updateActiveMenu(contentType);

            // 컨텐츠 로드
            await loadContent(contentType);
        });
    });

    // 메뉴 활성화 상태 업데이트
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

    // 컨텐츠 페이드 아웃
    function fadeOutContent(element) {
        return new Promise(resolve => {
            element.style.opacity = '1';
            element.style.transition = 'opacity 0.3s ease';

            requestAnimationFrame(() => {
                element.style.opacity = '0';
            });

            setTimeout(() => {
                resolve();
            }, 300);
        });
    }

    // 컨텐츠 페이드 인
    function fadeInContent(element) {
        element.style.opacity = '0';
        element.style.transition = 'opacity 0.3s ease';

        requestAnimationFrame(() => {
            element.style.opacity = '1';
        });
    }

    // 컨텐츠 로드
    async function loadContent(contentType) {
        if (isLoading) return;

        const contentArea = document.getElementById('profileContent');

        try {
            isLoading = true;
            currentTransition = fadeOutContent(contentArea);
            await currentTransition;

            showLoading(contentArea);
            fadeInContent(contentArea);

            const response = await fetch(`/user/profile/${contentType}`, {
                method: 'GET',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error('컨텐츠를 불러오는데 실패했습니다.');
            }

            await fadeOutContent(contentArea);
            contentArea.innerHTML = await response.text();
            fadeInContent(contentArea);

            // statistics 페이지인 경우
            if (contentType === 'statistics') {
                await loadStatisticsScript();
            }

            currentContent = contentType;

        } catch (error) {
            await fadeOutContent(contentArea);
            showError(contentArea, error.message);
            fadeInContent(contentArea);
        } finally {
            isLoading = false;
            currentTransition = null;
        }
    }

    // Statistics 스크립트 로드
    function loadStatisticsScript() {
        return new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = '/js/statistics.js';
            script.onload = () => {
                if (typeof window.initializeStatistics === 'function') {
                    window.initializeStatistics();
                }
                resolve();
            };
            script.onerror = reject;
            document.body.appendChild(script);
        });
    }

    // 초기 컨텐츠 로드
    function loadInitialContent() {
        const path = window.location.pathname;
        const match = path.match(/\/user\/profile\/([^\/]+)/);
        const contentType = match ? match[1] : 'my-questions';

        updateActiveMenu(contentType);
        loadContent(contentType);
    }


    // 로딩 표시
    function showLoading(container) {
        container.innerHTML = `
            <div class="loading">
                <div class="loading-spinner"></div>
            </div>
        `;
    }

    // 에러 표시
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

    // 브라우저 네비게이션 이벤트 처리
    window.addEventListener('popstate', (event) => {
        if (event.state && event.state.contentType) {
            updateActiveMenu(event.state.contentType);
            loadContent(event.state.contentType);
        }
    });
});

// 모달 관련 함수들
const modal = {
    element: null,

    init() {
        this.element = document.getElementById('deactivateModal');
        this.bindEvents();
    },

    bindEvents() {
        // 모달 외부 클릭시 닫기
        window.addEventListener('click', (event) => {
            if (event.target === this.element) {
                this.close();
            }
        });

        // ESC 키로 닫기
        window.addEventListener('keydown', (event) => {
            if (event.key === 'Escape' && this.element.style.display === 'block') {
                this.close();
            }
        });
    },

    show() {
        this.element.style.display = 'block';
        requestAnimationFrame(() => {
            this.element.classList.add('show');
        });

        // 포커스 트랩 설정
        const focusableElements = this.element.querySelectorAll(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );

        if (focusableElements.length) {
            focusableElements[0].focus();
        }
    },

    close() {
        this.element.classList.remove('show');
        setTimeout(() => {
            this.element.style.display = 'none';
        }, 300);
    }
};

// 회원 탈퇴 모달 초기화
document.addEventListener('DOMContentLoaded', () => {
    modal.init();
});

function confirmDelete() {
    modal.show();
}

function closeDeactivateModal() {
    modal.close();
}