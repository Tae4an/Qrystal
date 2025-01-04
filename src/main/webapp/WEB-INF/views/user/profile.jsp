<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>프로필 - Qrystal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<main class="profile-container">
    <h2>프로필 정보</h2>
    <div class="profile-content">
        <div class="profile-info">
            <p>
                <strong>이름:</strong>
                <span>${user.name}</span>
            </p>
            <p>
                <strong>이메일:</strong>
                <span>${user.email}</span>
            </p>
            <p>
                <strong>가입일:</strong>
                <span>${user.formattedCreatedAt}</span>
            </p>
        </div>

        <div class="profile-actions">
            <a href="/user/edit" class="btn btn-edit">프로필 수정</a>
            <a href="/user/password" class="btn btn-password">비밀번호 변경</a>
            <button type="button" class="btn btn-delete" onclick="confirmDelete()">회원 탈퇴</button>
        </div>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />

<script>
    function confirmDelete() {
        if (confirm('정말 탈퇴하시겠습니까?')) {
            location.href = '/user/delete';
        }
    }
</script>
</body>
</html>