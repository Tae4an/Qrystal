<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>관리자 로그인 - Qrystal Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/css/admin/auth.css'/>">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <h1>Qrystal Admin</h1>
            <p>관리자 로그인</p>
        </div>

        <c:if test="${error != null}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i> ${error}
            </div>
        </c:if>

        <form action="/login" method="post" class="login-form">
            <div class="form-group">
                <label for="adminId">
                    <i class="fas fa-user"></i> 아이디
                </label>
                <input type="text" id="adminId" name="adminId" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">
                    <i class="fas fa-lock"></i> 비밀번호
                </label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn btn-login">
                <i class="fas fa-sign-in-alt"></i> 로그인
            </button>
        </form>
    </div>
</div>
</body>
</html>