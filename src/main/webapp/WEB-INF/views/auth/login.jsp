<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 - Qrystal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />
<main class="login-container">
    <h2>로그인</h2>
    <form action="/auth/login" method="post">
        <div class="form-group">
            <label>이메일</label>
            <input type="email" name="email" required>
        </div>
        <div class="form-group">
            <label>비밀번호</label>
            <input type="password" name="password" required>
        </div>
        <div class="button-group">
            <button type="submit" class="btn btn-primary">로그인</button>
            <a href="/auth/signup" class="btn btn-secondary">회원가입</a>
        </div>
    </form>
</main>
<jsp:include page="../common/footer.jsp" />
<script src="<c:url value='/js/main.js'/>"></script>
</body>
</html>