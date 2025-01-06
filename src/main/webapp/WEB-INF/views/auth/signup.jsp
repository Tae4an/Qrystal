<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 - Qrystal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/signup.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />
<main class="signup-container">
    <h2>회원가입</h2>
    <form:form action="/auth/signup" method="post" modelAttribute="userSignupRequest" id="signupForm">
        <div class="form-group">
            <label for="email">이메일</label>
            <div class="email-group">
                <form:input path="email" type="email" id="email" class="email-input"/>
                <button type="button" id="verifyEmailBtn" class="btn btn-secondary">이메일 인증</button>
            </div>
            <span id="emailValidation" class="error-message" style="display: none"></span>
            <span id="emailStatus" class="status-message"></span>
            <div id="verificationArea" class="verification-area" style="display: none;">
                <input type="text" id="verificationCode" placeholder="인증코드 6자리 입력" maxlength="6"/>
                <span id="verificationTimer" class="timer"></span>
                <button type="button" id="verifyCodeBtn" class="btn btn-secondary">확인</button>
                <span id="verificationStatus" class="status-message"></span>
            </div>
        </div>

        <div class="form-group">
            <label for="password">비밀번호</label>
            <form:password path="password" id="password" />
            <form:errors path="password" cssClass="error-message" />
            <span id="passwordGuide" class="guide-message">8자 이상, 영문, 숫자, 특수문자를 포함해주세요.</span>
        </div>

        <div class="form-group">
            <label for="passwordConfirm">비밀번호 확인</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" />
            <span id="passwordMatch" class="status-message"></span>
        </div>

        <div class="form-group">
            <label for="name">이름</label>
            <form:input path="name" id="name" />
            <form:errors path="name" cssClass="error-message" />
        </div>

        <div class="button-group">
            <button type="submit" class="btn btn-primary" id="signupBtn">가입하기</button>
            <a href="/auth/login" class="btn btn-secondary">로그인으로 돌아가기</a>
        </div>
    </form:form>
</main>
<jsp:include page="../common/footer.jsp" />
<script src="<c:url value='/js/signup.js'/>"></script>
</body>
</html>