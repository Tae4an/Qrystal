<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로필 - Qrystal</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/profile.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<main class="profile-container">
    <div class="profile-header">
        <h2>프로필 정보</h2>
    </div>

    <div class="profile-card">
        <div class="profile-avatar">
            <i class="fas fa-user"></i>
        </div>

        <div class="profile-info">
            <div class="info-group">
                <span class="info-label">이름</span>
                <span class="info-value">${user.name}</span>
            </div>
            <div class="info-group">
                <span class="info-label">이메일</span>
                <span class="info-value">${user.email}</span>
            </div>
            <div class="info-group">
                <span class="info-label">가입일</span>
                <span class="info-value">${user.formattedCreatedAt}</span>
            </div>
        </div>

        <div class="profile-actions">
            <a href="/user/edit" class="btn btn-edit">
                <i class="fas fa-pen"></i> 프로필 수정
            </a>
            <a href="/user/password" class="btn btn-password">
                <i class="fas fa-key"></i> 비밀번호 변경
            </a>
            <button type="button" class="btn btn-delete" onclick="confirmDelete()">
                <i class="fas fa-user-minus"></i> 회원 탈퇴
            </button>
        </div>
    </div>
</main>

<!-- 회원 탈퇴 모달 -->
<div id="deactivateModal" class="modal" style="display: none;">
    <div class="modal-content">
        <h3>회원 탈퇴</h3>
        <p>정말 탈퇴하시겠습니까?</p>
        <form action="/user/deactivate" method="post">
            <c:if test="${user.provider == null}">
                <div class="form-group">
                    <label for="password">비밀번호를 입력하세요</label>
                    <input type="password" id="password" name="password" required>
                </div>
            </c:if>
            <div class="form-actions">
                <button type="submit" class="btn btn-delete">탈퇴하기</button>
                <button type="button" class="btn btn-cancel" onclick="closeDeactivateModal()">취소</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    function confirmDelete() {
        document.getElementById('deactivateModal').style.display = 'block';
    }

    function closeDeactivateModal() {
        document.getElementById('deactivateModal').style.display = 'none';
    }

    // 모달 외부 클릭시 닫기
    window.onclick = function(event) {
        var modal = document.getElementById('deactivateModal');
        if (event.target == modal) {
            closeDeactivateModal();
        }
    }
</script>
</body>
</html>