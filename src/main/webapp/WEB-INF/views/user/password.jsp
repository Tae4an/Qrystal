<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<link rel="stylesheet" type="text/css" href="<c:url value='/css/profile.css'/>">

<main class="profile-container">
    <div class="profile-header">
        <h2>비밀번호 변경</h2>
    </div>
    <div class="profile-card">
        <form action="/user/password" method="post" class="password-form" onsubmit="return validateForm()">
            <c:if test="${error != null}">
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>
            <div class="form-group">
                <label for="currentPassword">
                    <i class="fas fa-lock"></i> 현재 비밀번호
                </label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>
            <div class="form-group">
                <label for="newPassword">
                    <i class="fas fa-key"></i> 새 비밀번호
                </label>
                <input type="password"
                       id="newPassword"
                       name="newPassword"
                       pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$"
                       onkeyup="checkPasswordStrength(this.value)"
                       required>
                <div class="password-strength">
                    <div id="strengthBar" class="password-strength-bar"></div>
                </div>
                <small class="error-message" id="passwordHint" style="display: none;">
                    비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다
                </small>
            </div>
            <div class="form-group">
                <label for="confirmPassword">
                    <i class="fas fa-check"></i> 새 비밀번호 확인
                </label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
                <small class="error-message" id="confirmHint" style="display: none;">
                    비밀번호가 일치하지 않습니다
                </small>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-password">
                    <i class="fas fa-save"></i> 비밀번호 변경
                </button>
                <a href="/user/profile" class="btn btn-cancel">
                    <i class="fas fa-times"></i> 취소
                </a>
            </div>
        </form>
    </div>
</main>

<script src="<c:url value='/js/main.js'/>"></script>