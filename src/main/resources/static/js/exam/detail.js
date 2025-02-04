document.addEventListener('DOMContentLoaded', function() {
    const startExamButton = document.getElementById('startExam');

    startExamButton.addEventListener('click', async function() {
        try {
            const response = await fetch(`/api/exams/${examId}/start`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const attemptId = await response.json();
                window.location.href = `/exams/${examId}/attempt?attemptId=${attemptId}`;
            } else {
                const error = await response.json();
                alert(error.message || '시험을 시작할 수 없습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('시험 시작 중 오류가 발생했습니다.');
        }
    });
});