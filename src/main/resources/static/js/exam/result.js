document.addEventListener('DOMContentLoaded', function() {
    // 정답/오답 색상 설정
    const questionResults = document.querySelectorAll('.question-result');
    questionResults.forEach(result => {
        const score = parseInt(result.querySelector('.question-score span').textContent);
        const maxScore = parseInt(result.querySelector('.score-denominator').textContent.replace('/ ', ''));

        if (score === maxScore) {
            result.classList.add('correct');
        } else {
            result.classList.add('incorrect');
        }
    });

    // 객관식 문제의 선택지 표시
    const choiceItems = document.querySelectorAll('.choice-item');
    choiceItems.forEach(item => {
        if (item.classList.contains('correct')) {
            item.querySelector('i').classList.add('fa-check');
        } else if (item.classList.contains('incorrect')) {
            item.querySelector('i').classList.add('fa-times');
        }
    });

    // 다시 풀기 버튼 이벤트
    const retryButton = document.querySelector('.result-actions .btn-primary');
    if (retryButton) {
        retryButton.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('같은 시험을 다시 응시하시겠습니까?')) {
                window.location.href = `/exams/${examId}`;
            }
        });
    }

    // 점수 애니메이션 효과
    function animateValue(element, start, end, duration) {
        let current = start;
        const range = end - start;
        const increment = range / (duration / 16);
        const timer = setInterval(() => {
            current += increment;
            if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
                clearInterval(timer);
                current = end;
            }
            element.textContent = Math.round(current);
        }, 16);
    }

    // 총점 애니메이션
    const totalScoreElement = document.querySelector('.score-value span');
    const totalScore = parseInt(totalScoreElement.textContent);
    animateValue(totalScoreElement, 0, totalScore, 1000);

    // 정답률 차트 (선택사항)
    const correctRate = parseFloat(document.querySelector('.detail-item:last-child span:last-child').textContent);
    if (correctRate !== null) {
        const ctx = document.getElementById('correctRateChart');
        if (ctx) {
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['정답', '오답'],
                    datasets: [{
                        data: [correctRate, 100 - correctRate],
                        backgroundColor: ['#4CAF50', '#f44336']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    }

    // 답안 비교 토글 (긴 답안의 경우)
    const answerComparisons = document.querySelectorAll('.answer-comparison');
    answerComparisons.forEach(comparison => {
        const answers = comparison.querySelectorAll('p');
        answers.forEach(answer => {
            if (answer.scrollHeight > 100) {
                answer.classList.add('expandable');
                answer.addEventListener('click', () => {
                    answer.classList.toggle('expanded');
                });
            }
        });
    });

    // 해설 토글
    const explanations = document.querySelectorAll('.answer-explanation');
    explanations.forEach(explanation => {
        const content = explanation.querySelector('p');
        if (content.scrollHeight > 100) {
            const toggleButton = document.createElement('button');
            toggleButton.className = 'btn btn-link toggle-explanation';
            toggleButton.textContent = '해설 더보기';

            toggleButton.addEventListener('click', () => {
                content.classList.toggle('expanded');
                toggleButton.textContent = content.classList.contains('expanded') ? '해설 접기' : '해설 더보기';
            });

            explanation.appendChild(toggleButton);
        }
    });
});