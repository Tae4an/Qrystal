// 전역 함수로 선언하여 외부에서 접근 가능하게 함
window.initializeStatistics = function() {
    // 전역 변수
    let currentCharts = [];  // 현재 표시된 차트들을 저장

    // 통계 유형 선택 시 이벤트
    const statsTypeSelect = document.getElementById('statsType');
    const examSelector = document.getElementById('examSelector');
    const examSelect = document.getElementById('examId');

    if (statsTypeSelect) {
        statsTypeSelect.addEventListener('change', function() {
            const selectedType = this.value;

            if (selectedType === 'byExam') {
                examSelector.style.display = 'block';
                if (examSelect.value) {
                    loadExamStatistics(examSelect.value);
                }
            } else {
                examSelector.style.display = 'none';
                loadOverallStatistics();
            }
        });
    }

    // 시험 선택 시 이벤트
    if (examSelect) {
        examSelect.addEventListener('change', function() {
            if (this.value) {
                loadExamStatistics(this.value);
            }
        });
    }

    // 초기 통계 로드
    loadOverallStatistics();

    // 전체 통계 로드
    async function loadOverallStatistics() {
        try {
            showLoading();
            const response = await fetch('/api/statistics/overall');
            if (!response.ok) throw new Error('통계 데이터를 불러오는데 실패했습니다.');
            const data = await response.json();
            renderStatistics(data, '전체');
        } catch (error) {
            showError(error.message);
        } finally {
            hideLoading();
        }
    }

    // 특정 시험의 통계 로드
    async function loadExamStatistics(examId) {
        try {
            showLoading();
            const response = await fetch(`/api/statistics/exam/${examId}`);
            if (!response.ok) throw new Error('시험 통계를 불러오는데 실패했습니다.');
            const data = await response.json();
            const examTitle = examSelect.options[examSelect.selectedIndex].text;
            renderStatistics(data, examTitle);
        } catch (error) {
            showError(error.message);
        } finally {
            hideLoading();
        }
    }

    // 통계 렌더링
    function renderStatistics(data, title) {
        clearCharts();
        const container = document.getElementById('statsContent');

        // 통계 요약 카드 렌더링
        container.innerHTML = `
           <div class="stats-summary">
               <div class="stat-card">
                   <h3>총 응시 횟수</h3>
                   <p class="stat-value">${data.totalAttempts}회</p>
               </div>
               <div class="stat-card">
                   <h3>평균 점수</h3>
                   <p class="stat-value">${data.averageScore.toFixed(1)}점</p>
               </div>
               <div class="stat-card">
                   <h3>평균 정답률</h3>
                   <p class="stat-value">${data.averageCorrectRate.toFixed(1)}%</p>
               </div>
           </div>
           <div class="charts-container">
               <div class="chart-card">
                   <h3>월별 응시 현황</h3>
                   <div class="chart-container">
                       <canvas id="monthlyChart"></canvas>
                   </div>
               </div>
               <div class="chart-card">
                   <h3>정답률 분포</h3>
                   <div class="chart-container">
                       <canvas id="distributionChart"></canvas>
                   </div>
               </div>
           </div>
       `;

        // 월별 추이 차트
        const monthlyCtx = document.getElementById('monthlyChart').getContext('2d');
        currentCharts.push(new Chart(monthlyCtx, {
            type: 'line',
            data: {
                labels: data.monthlyData.map(d => d.month),
                datasets: [
                    {
                        label: '응시 횟수',
                        data: data.monthlyData.map(d => d.totalAttempts),
                        borderColor: '#4A90E2',
                        backgroundColor: 'rgba(74, 144, 226, 0.1)',
                        yAxisID: 'y1'
                    },
                    {
                        label: '평균 정답률',
                        data: data.monthlyData.map(d => d.averageCorrectRate),
                        borderColor: '#82C91E',
                        backgroundColor: 'rgba(130, 201, 30, 0.1)',
                        yAxisID: 'y2'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y1: {
                        type: 'linear',
                        position: 'left',
                        title: {
                            display: true,
                            text: '응시 횟수'
                        }
                    },
                    y2: {
                        type: 'linear',
                        position: 'right',
                        title: {
                            display: true,
                            text: '정답률 (%)'
                        }
                    }
                }
            }
        }));

        // 정답률 분포 차트
        const distributionCtx = document.getElementById('distributionChart').getContext('2d');
        const distributionData = data.correctRateDistribution;
        currentCharts.push(new Chart(distributionCtx, {
            type: 'bar',
            data: {
                labels: Object.keys(distributionData),
                datasets: [{
                    label: '응시 횟수',
                    data: Object.values(distributionData),
                    backgroundColor: '#4A90E2'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: '시험 수'
                        }
                    }
                }
            }
        }));
    }

    // 기존 차트 제거
    function clearCharts() {
        currentCharts.forEach(chart => chart.destroy());
        currentCharts = [];
    }

    // 로딩 표시
    function showLoading() {
        const container = document.getElementById('statsContent');
        container.innerHTML = `
           <div class="loading">
               <div class="loading-spinner"></div>
           </div>
       `;
    }

    // 에러 표시
    function showError(message) {
        const container = document.getElementById('statsContent');
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

    // 로딩 숨기기
    function hideLoading() {
        // 로딩이 끝나면 컨텐츠가 자동으로 교체되므로 별도 처리 불필요
    }
}

// 페이지 로드 시에도 실행
document.addEventListener('DOMContentLoaded', function() {
    window.initializeStatistics();
});