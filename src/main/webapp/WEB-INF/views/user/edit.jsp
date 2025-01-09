<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로필 수정 - Qrystal</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/profile.css'/>">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<main class="profile-container">
    <div class="profile-header">
        <h2>프로필 수정</h2>
    </div>

    <div class="profile-card">
        <div class="profile-avatar">
            <i class="fas fa-user"></i>
        </div>

        <form action="/user/edit" method="post" class="edit-form">
            <c:if test="${error != null}">
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <div class="form-group">
                <label for="email">이메일</label>
                <input type="email" id="email" value="${user.email}" disabled
                       class="form-control disabled">
                <small class="form-text">이메일은 변경할 수 없습니다</small>
            </div>

            <div class="form-group">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" value="${user.name}"
                       class="form-control"
                       minlength="2" maxlength="20" required>
                <small class="form-text">2~20자 이내로 입력해주세요</small>
            </div>

            <div class="form-group">
                <label>가입 유형</label>
                <div class="provider-info">
                    <c:choose>
                        <c:when test="${user.provider == null}">
                                <span class="provider-badge email">
                                    <i class="fas fa-envelope"></i> 이메일 계정
                                </span>
                        </c:when>
                        <c:otherwise>
                                <span class="provider-badge ${user.provider.toLowerCase()}">
                                    <i class="fab fa-${user.provider.toLowerCase()}"></i>
                                    ${user.provider} 계정
                                </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-save">
                    <i class="fas fa-save"></i> 저장하기
                </button>
                <a href="/user/profile" class="btn btn-cancel">
                    <i class="fas fa-times"></i> 취소
                </a>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
</body>
</html>