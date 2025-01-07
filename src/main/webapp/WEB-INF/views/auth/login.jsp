<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 - Qrystal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/login.css'/>">

</head>
<body>
<jsp:include page="../common/header.jsp" />
<main class="login-container">
    <h2>로그인</h2>
    <form action="/auth/login" method="post" id="loginForm">
        <div class="form-group">
            <label for="email">이메일</label>
            <input type="email" id="email" name="email" required>
            <span id="emailValidation" class="error-message" style="display: none"></span>
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" required>
            <span id="passwordValidation" class="error-message" style="display: none"></span>
        </div>
        <div class="button-group">
            <button type="submit" class="btn btn-primary">로그인</button>
        </div>
    </form>

    <div class="social-login">
        <div class="social-buttons">
            <a href="/oauth2/authorization/google">
                <img src="<c:url value='/img/google.png'/>" alt="Google 로그인" class="social-login-btn">
            </a>
            <a href="/oauth2/authorization/github">
                <img src="<c:url value='/img/github.png'/>" alt="GitHub 로그인" class="social-login-btn">
            </a>
        </div>
    </div>

    <div class="signup-link">
        <p>계정이 없으신가요? <a href="/auth/signup">회원가입</a></p>
    </div>
</main>
<jsp:include page="../common/footer.jsp" />
<script src="<c:url value='/js/login.js'/>"></script>
</body>
</html>