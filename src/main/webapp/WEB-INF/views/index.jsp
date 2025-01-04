<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Qrystal - 모의고사 플랫폼</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<jsp:include page="common/header.jsp" />

<main>
    <section class="main-banner">
        <h1>Qrystal 모의고사 플랫폼</h1>
        <p>자격증 취득을 위한 최적의 학습 파트너</p>
        <div class="main-buttons">
            <a href="/exam/list" class="main-btn">문제 은행</a>
            <a href="/exam/mock" class="main-btn">모의고사</a>
            <a href="/exam/analysis" class="main-btn">성적 분석</a>
        </div>
    </section>
</main>

<jsp:include page="common/footer.jsp" />
<script src="<c:url value='/js/main.js'/>"></script>
</body>
</html>