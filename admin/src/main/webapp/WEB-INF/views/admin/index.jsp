<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Qrystal Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/css/admin/index.css'/>">
</head>
<body>
<div class="admin-container">
    <!-- 사이드바 -->
    <nav id="sidebar" class="sidebar">
        <div class="sidebar-header">
            <h1>Qrystal Admin</h1>
        </div>
        <ul class="sidebar-menu">
            <li>
                <a href="/"><i class="fas fa-home"></i> 대시보드</a>
            </li>
            <li>
                <a href="/admins"><i class="fas fa-user-shield"></i> 관리자 관리</a>
            </li>
            <li>
                <a href="/users"><i class="fas fa-users"></i> 사용자 관리</a>
            </li>
        </ul>
    </nav>

    <!-- 메인 컨텐츠 -->
    <div class="main-content">
        <jsp:include page="common/header.jsp" />
        <!-- 동적 컨텐츠 영역 -->
        <main id="content" class="content">
            <c:choose>
                <c:when test="${content == null}">
                    <jsp:include page="dashboard.jsp"/>
                </c:when>
                <c:otherwise>
                    <jsp:include page="${content}.jsp"/>
                </c:otherwise>
            </c:choose>
        </main>
        <jsp:include page="common/footer.jsp" />
    </div>
</div>

<script src="<c:url value='/js/admin/index.js'/>"></script>
</body>
</html>