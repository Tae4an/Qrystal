let currentExams = [];  // 현재 표시된 모의고사 목록 저장

document.addEventListener('DOMContentLoaded', function() {
    initializeMyExams();

    // 검색어 입력 이벤트
    document.getElementById('searchInput').addEventListener('input', filterExams);
    // 상태 필터 변경 이벤트
    document.getElementById('statusFilter').addEventListener('change', filterExams);
});

async function initializeMyExams() {
    try {
        const response = await fetch('/api/exams/my', {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) throw new Error('모의고사 목록을 불러올 수 없습니다.');

        currentExams = await response.json();
        renderExamList(currentExams);
    } catch (error) {
        console.error('Error:', error);
        alert('모의고사 목록을 불러오는데 실패했습니다.');
    }
}
// renderExamList 함수 수정
function renderExamList(exams) {
    const container = document.getElementById('examsList');
    if (!exams || exams.length === 0) {
        container.innerHTML = '<div class="no-data">등록된 모의고사가 없습니다.</div>';
        return;
    }

    container.innerHTML = exams.map(exam => `
        <div class="exam-card">
            <div class="exam-card-header">
                <div class="exam-info">
                    <h3 class="exam-title">${exam.title}</h3>
                    <p class="exam-description">${exam.description || ''}</p>
                    <div class="exam-meta">
                        <span class="category-path">
                            <i class="fas fa-folder"></i>
                            ${exam.categoryName || '미분류'}
                        </span>
                        <span class="question-count">
                            <i class="fas fa-list-ol"></i>
                            ${exam.questions.length}문제
                        </span>
                        <span class="total-points">
                            <i class="fas fa-trophy"></i>
                            ${exam.totalPoints}점
                        </span>
                        <span class="time-limit">
                            <i class="fas fa-clock"></i>
                            ${exam.timeLimit}분
                        </span>
                        <span class="exam-status">
                            <i class="fas fa-${exam.isPublic ? 'globe' : 'lock'}"></i>
                            ${exam.isPublic ? '공개' : '비공개'}
                        </span>
                    </div>
                </div>
            </div>
            <div class="exam-actions">
                <button type="button" class="btn btn-edit" onclick="editExam(${exam.id})">
                    <i class="fas fa-edit"></i> 수정
                </button>
                <button type="button" class="btn btn-delete" onclick="deleteExam(${exam.id})">
                    <i class="fas fa-trash"></i> 삭제
                </button>
            </div>
        </div>
    `).join('');
}


// 모의고사 수정 페이지로 이동
function editExam(examId) {
    window.location.href = `/exams/${examId}/edit`;
}

// 모의고사 삭제
async function deleteExam(examId) {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    try {
        const response = await fetch(`/api/exams/${examId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        });

        // 204 No Content나 200 OK 모두 성공으로 처리
        if (response.status === 204 || response.ok) {
            // UI에서 해당 모의고사 제거
            currentExams = currentExams.filter(exam => exam.id !== examId);
            renderExamList(currentExams);
            alert('모의고사가 삭제되었습니다.');
        } else {
            throw new Error('삭제 중 오류가 발생했습니다.');
        }

    } catch (error) {
        console.error('Error:', error);
        alert('삭제 중 오류가 발생했습니다.');
    }
}

// 검색 및 필터링
function filterExams() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const statusFilter = document.getElementById('statusFilter').value;

    const filteredExams = currentExams.filter(exam => {
        const matchesSearch = exam.title.toLowerCase().includes(searchTerm) ||
            (exam.description && exam.description.toLowerCase().includes(searchTerm));
        const matchesStatus = !statusFilter || exam.status === statusFilter;

        return matchesSearch && matchesStatus;
    });

    renderExamList(filteredExams);
}