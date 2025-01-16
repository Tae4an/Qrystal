document.addEventListener('DOMContentLoaded', function() {
    // 모달 관련 요소
    const modal = document.getElementById('adminModal');
    const modalTitle = document.getElementById('modalTitle');
    const adminForm = document.getElementById('adminForm');

    // 모달 열기 - 관리자 추가
    window.openCreateAdminModal = function() {
        modalTitle.textContent = "관리자 추가";
        adminForm.reset();
        adminForm.dataset.mode = 'create';
        modal.style.display = 'block';
        // 애니메이션을 위한 지연
        setTimeout(() => {
            modal.classList.add('show');
        }, 10);
    }

    // 모달 열기 - 관리자 수정
    window.openEditAdminModal = function(id) {
        modalTitle.textContent = "관리자 수정";
        adminForm.dataset.mode = 'edit';
        adminForm.dataset.adminId = id;

        // 관리자 정보 조회
        fetch(`/api/admin/admins/${id}`)
            .then(response => response.json())
            .then(admin => {
                document.getElementById('adminIdInput').value = admin.adminId;
                document.getElementById('adminIdInput').disabled = true;
                document.getElementById('nameInput').value = admin.name;
                document.getElementById('roleSelect').value = admin.role;
                document.getElementById('passwordInput').value = '';

                modal.style.display = 'block';
                setTimeout(() => {
                    modal.classList.add('show');
                }, 10);
            });
    }

    // 모달 닫기
    window.closeAdminModal = function() {
        modal.classList.remove('show');
        setTimeout(() => {
            modal.style.display = 'none';
            adminForm.reset();
            document.getElementById('adminIdInput').disabled = false;
        }, 300); // CSS 트랜지션 시간과 일치
    }

    // 모달 외부 클릭시 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeAdminModal();
        }
    });

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.style.display === 'block') {
            closeAdminModal();
        }
    });

    // 관리자 상태 변경
    window.toggleAdminStatus = function(id, status) {
        if(!confirm(`관리자를 ${status === 'ACTIVE' ? '활성화' : '비활성화'} 하시겠습니까?`)) {
            return;
        }

        fetch(`/api/admin/admins/${id}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
            },
            body: JSON.stringify({status: status})
        })
            .then(response => {
                if(response.ok) {
                    // 성공 애니메이션 후 페이지 새로고침
                    const row = document.querySelector(`tr[data-admin-id="${id}"]`);
                    row.style.backgroundColor = status === 'ACTIVE' ? '#10B98133' : '#EF444433';
                    setTimeout(() => {
                        location.reload();
                    }, 500);
                } else {
                    throw new Error('상태 변경 실패');
                }
            })
            .catch(error => {
                alert('관리자 상태 변경에 실패했습니다.');
            });
    }

    // 폼 제출 처리
    window.submitAdminForm = function() {
        const formData = {
            adminId: document.getElementById('adminIdInput').value,
            password: document.getElementById('passwordInput').value,
            name: document.getElementById('nameInput').value,
            role: document.getElementById('roleSelect').value
        };

        const mode = adminForm.dataset.mode;
        const url = mode === 'create' ? '/api/admin/admins' : `/api/admin/admins/${adminForm.dataset.adminId}`;
        const method = mode === 'create' ? 'POST' : 'PUT';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.content
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if(response.ok) {
                    // 성공 메시지와 함께 모달 닫기
                    showToast('성공적으로 저장되었습니다.');
                    closeAdminModal();
                    setTimeout(() => {
                        location.reload();
                    }, 500);
                } else {
                    throw new Error('저장 실패');
                }
            })
            .catch(error => {
                showToast('저장에 실패했습니다.', 'error');
            });
    }

    // 토스트 메시지 표시
    function showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;

        document.body.appendChild(toast);

        // 애니메이션을 위한 지연
        setTimeout(() => {
            toast.classList.add('show');
        }, 10);

        // 3초 후 제거
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(toast);
            }, 300);
        }, 3000);
    }
});