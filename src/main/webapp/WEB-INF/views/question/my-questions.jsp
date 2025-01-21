<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/question/my-questions.css'/>">

<div class="my-questions-container">
    <!-- 상단 헤더 -->
    <div class="content-header">
        <h2>내 문제 관리</h2>
        <button type="button" class="btn btn-primary" onclick="location.href='/questions/new'">
            <i class="fas fa-plus"></i> 새 문제
        </button>
    </div>

    <!-- 필터링 영역 -->
    <div class="filter-section">
        <div class="search-filters">
            <input type="text" id="searchInput" class="form-control" placeholder="문제 검색...">
            <select id="typeFilter" class="form-control">
                <option value="">유형 선택</option>
                <option value="1">객관식</option>
                <option value="2">주관식</option>
                <option value="3">서술형</option>
            </select>
            <select id="categoryFilter" class="form-control">
                <option value="">카테고리 선택</option>
                <!-- JavaScript로 카테고리 옵션 추가 -->
            </select>
        </div>
    </div>

    <!-- 문제 목록 -->
    <div class="questions-list" id="myQuestionsList">
        <!-- JavaScript로 동적 렌더링 -->
    </div>
</div>

<script src="<c:url value='/js/question/my-questions.js'/>"></script>