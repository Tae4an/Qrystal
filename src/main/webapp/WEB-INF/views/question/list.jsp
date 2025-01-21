<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/question/list.css'/>">

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
        <!-- 검색 및 필터 영역 -->
        <div class="content-header">
            <div class="search-filters">
                <input type="text" id="searchInput" class="form-control" placeholder="문제 검색...">
                <select id="typeFilter" class="form-control">
                    <option value="">유형 선택</option>
                    <option value="1">객관식</option>
                    <option value="2">주관식</option>
                    <option value="3">서술형</option>
                </select>
                <select id="difficultyFilter" class="form-control">
                    <option value="">난이도 선택</option>
                    <option value="1">★</option>
                    <option value="2">★★</option>
                    <option value="3">★★★</option>
                    <option value="4">★★★★</option>
                    <option value="5">★★★★★</option>
                </select>
            </div>
            <div class="header-actions">
                <button type="button" class="btn btn-secondary" onclick="location.href='/questions/my'">
                    <i class="fas fa-list"></i> 내 문제
                </button>
                <button type="button" class="btn btn-primary" onclick="location.href='/questions/new'">
                    <i class="fas fa-plus"></i> 새 문제
                </button>
            </div>
        </div>

        <!-- 문제 목록 -->
        <div class="questions-list" id="questionsList">
            <!-- JavaScript로 문제 목록 렌더링 -->
        </div>
    </div>
</div>

<script src="<c:url value='/js/question/list.js'/>"></script>