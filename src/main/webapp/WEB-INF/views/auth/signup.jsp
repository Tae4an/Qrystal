<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 - Qrystal</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />
<main class="signup-container">
    <h2>회원가입</h2>
    <form:form action="/auth/signup" method="post" modelAttribute="userSignupRequest">
        <div class="form-group">
            <label for="email">이메일</label>
            <form:input path="email" type="email" id="email" />
            <form:errors path="email" cssClass="error-message" />
        </div>
        <div class="form-group">
            <label for="password">비밀번호</label>
            <form:password path="password" id="password" />
            <form:errors path="password" cssClass="error-message" />
        </div>
        <div class="form-group">
            <label for="passwordConfirm">비밀번호 확인</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" />
        </div>
        <div class="form-group">
            <label for="name">이름</label>
            <form:input path="name" id="name" />
            <form:errors path="name" cssClass="error-message" />
        </div>
        <div class="button-group">
            <button type="submit" class="btn btn-primary">가입하기</button>
            <a href="/auth/login" class="btn btn-secondary">로그인으로 돌아가기</a>
        </div>
    </form:form>
</main>
<jsp:include page="../common/footer.jsp" />
<script src="<c:url value='/js/main.js'/>"></script>
</body>
</html>