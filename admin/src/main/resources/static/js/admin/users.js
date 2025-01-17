document.addEventListener('DOMContentLoaded', function() {
    // 검색 디바운스 처리
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        const searchForm = document.getElementById('searchForm');
        const searchDebounce = debounce(() => {
            searchForm.submit();
        }, 500);
        searchInput.addEventListener('input', searchDebounce);
    }

    // 디바운스 유틸리티 함수
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
});

function updateUserStatus(userId, status) {
    if (!confirm('정말 변경하시겠습니까?')) {
        return;
    }

    fetch(`/api/admin/users/${userId}/status?status=${status}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) throw new Error('상태 변경 실패');
            window.location.reload();
        })
        .catch(error => {
            alert('상태 변경에 실패했습니다.');
        });
}