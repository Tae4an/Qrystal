<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/admin/category.css'/>">

<div class="category-container">
    <!-- 상단 헤더 -->
    <div class="content-header">
        <h2>카테고리 관리</h2>
        <button type="button" class="btn btn-primary" onclick="openCategoryModal()">
            <i class="fas fa-plus"></i> 새 카테고리
        </button>
    </div>

    <!-- 카테고리 트리 -->
    <div class="category-tree">
        <ul id="categoryList" class="tree-root">
            <!-- JavaScript로 동적 렌더링 -->
        </ul>
    </div>

    <!-- 카테고리 추가/수정 모달 -->
    <div id="categoryModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">카테고리 추가</h3>
            <form id="categoryForm">
                <input type="hidden" id="categoryId">
                <div class="form-group">
                    <label for="parentId">상위 카테고리</label>
                    <select id="parentId" class="form-control">
                        <option value="">최상위 카테고리</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="name">카테고리명</label>
                    <input type="text" id="name" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="description">설명</label>
                    <textarea id="description" class="form-control"></textarea>
                </div>
                <div class="form-group">
                    <label for="status">상태</label>
                    <select id="status" class="form-control">
                        <option value="ACTIVE">활성</option>
                        <option value="INACTIVE">비활성</option>
                    </select>
                </div>
                <div class="modal-buttons">
                    <button type="submit" class="btn btn-primary">저장</button>
                    <button type="button" class="btn btn-secondary" onclick="closeCategoryModal()">취소</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Toast Container -->
<div id="toastContainer"></div>

<script src="<c:url value='/js/admin/category.js'/>"></script>