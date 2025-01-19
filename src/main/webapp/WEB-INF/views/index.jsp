<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Qrystal - 모의고사 플랫폼</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/index.css'/>">
</head>
<body>
<div class="container">
    <!-- 헤더 포함 -->
    <jsp:include page="common/header.jsp" />

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <c:choose>
            <c:when test="${content == null}">
                <jsp:include page="home.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="${content}.jsp"/>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- 푸터 포함 -->
    <jsp:include page="common/footer.jsp" />
</div>

<script src="<c:url value='/js/index.js'/>"></script>
</body>
</html>