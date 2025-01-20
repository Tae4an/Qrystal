<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/question/list.css'/>">
<script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/4.17.21/lodash.min.js"></script>


<div class="question-container">
    <!-- 좌측: 카테고리 트리 -->
    <div class="category-sidebar">
        <div class="sidebar-header">
            <h3>카테고리</h3>
        </div>
        <div id="categoryTree" class="category-tree">
            <!-- JavaScript로 카테고리 트리 렌더링 -->
        </div>
    </div>

    <!-- 우측: 문제 목록 -->
    <div class="question-content">
        <!-- 상단: 검색 및 필터 -->
        <div class="content-header">
            <div class="search-filters">
                <input type="text" id="searchInput" placeholder="문제 검색..." class="search-input">
                <select id="typeFilter" class="filter-select">
                    <option value="">유형 선택</option>
                    <option value="1">객관식</option>
                    <option value="2">주관식</option>
                    <option value="3">서술형</option>
                </select>
                <select id="difficultyFilter" class="filter-select">
                    <option value="">난이도 선택</option>
                    <option value="1">★</option>
                    <option value="2">★★</option>
                    <option value="3">★★★</option>
                    <option value="4">★★★★</option>
                    <option value="5">★★★★★</option>
                </select>
            </div>
            <button type="button" class="btn btn-primary" onclick="location.href='/questions/new'">
                <i class="fas fa-plus"></i> 새 문제
            </button>
        </div>

        <!-- 문제 목록 테이블 -->
        <div class="question-table-container">
            <table class="question-table">
                <thead>
                <tr>
                    <th>제목</th>
                    <th>유형</th>
                    <th>난이도</th>
                    <th>작성자</th>
                    <th>작성일</th>
                    <th>상태</th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody id="questionList">
                <!-- JavaScript로 문제 목록 렌더링 -->
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="<c:url value='/js/question/list.js'/>"></script>