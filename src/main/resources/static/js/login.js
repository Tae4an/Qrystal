document.addEventListener('DOMContentLoaded', function() {
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