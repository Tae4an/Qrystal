document.addEventListener('DOMContentLoaded', function() {
    // 리다이렉트된 경우의 알림 메시지 처리
    const loginMessage = document.getElementById('loginMessage');
    // value가 있고, 빈 문자열이 아닌 경우에만 alert 표시
    if (loginMessage && loginMessage.value && loginMessage.value.trim() !== '') {
        alert(loginMessage.value);
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const fields = [
                { id: 'email', name: '이메일' },
                { id: 'password', name: '비밀번호' }
            ];

            let isValid = true;
            let firstEmptyField = null;

            fields.forEach(field => {
                const element = document.getElementById(field.id);
                const validation = document.getElementById(`${field.id}Validation`);

                if (!element.value) {
                    isValid = false;
                    element.classList.add('invalid-input');
                    if (validation) {
                        validation.textContent = `${field.name}을(를) 입력해주세요.`;
                        validation.style.display = 'block';
                    }
                    if (!firstEmptyField) {
                        firstEmptyField = element;
                    }
                } else {
                    element.classList.remove('invalid-input');
                    if (validation) {
                        validation.style.display = 'none';
                    }
                }
            });

            if (!isValid) {
                firstEmptyField.focus();
                return;
            }

            // 모든 검증 통과시 폼 제출
            loginForm.submit();
        });
    }
});