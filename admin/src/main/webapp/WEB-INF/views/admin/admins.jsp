<!-- /WEB-INF/views/admin/admins.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="<c:url value='/css/admin/admins.css'/>">

<div class="admins">
    <div class="admins-header">
        <h2>관리자 관리</h2>
        <button class="btn btn-primary" onclick="openCreateAdminModal()">
            <i class="fas fa-plus"></i> 관리자 추가
        </button>
    </div>

    <div class="admins-content">
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>이름</th>
                <th>권한</th>
                <th>상태</th>
                <th>마지막 로그인</th>
                <th>생성일</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${adminList}" var="admin">
                <tr>
                    <td>${admin.adminId}</td>
                    <td>${admin.name}</td>
                    <td>
                            <span class="badge ${admin.role == 'SUPER_ADMIN' ? 'badge-primary' : 'badge-secondary'}">
                                    ${admin.role == 'SUPER_ADMIN' ? '최고 관리자' : '관리자'}
                            </span>
                    </td>
                    <td>
                            <span class="badge ${admin.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                    ${admin.status == 'ACTIVE' ? '활성' : '비활성'}
                            </span>
                    </td>
                    <td>${admin.lastLoginAt}</td>
                    <td>${admin.createdAt}</td>
                    <td>
                        <div class="btn-group">
                            <button class="btn btn-sm btn-outline-primary" onclick="openEditAdminModal(${admin.id})">
                                수정
                            </button>
                            <c:if test="${admin.role != 'SUPER_ADMIN'}">
                                <button class="btn btn-sm ${admin.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}"
                                        onclick="toggleAdminStatus(${admin.id}, '${admin.status == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}')">
                                        ${admin.status == 'ACTIVE' ? '비활성화' : '활성화'}
                                </button>
                            </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- 관리자 추가/수정 모달 -->
<div id="adminModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 id="modalTitle">관리자 추가</h3>
            <button class="close-button" onclick="closeAdminModal()">×</button>
        </div>
        <div class="modal-body">
            <form id="adminForm" onsubmit="return false;">
                <input type="hidden" id="adminId">
                <div class="form-group">
                    <label for="adminIdInput">관리자 ID</label>
                    <input type="text" id="adminIdInput" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="passwordInput">비밀번호</label>
                    <input type="password" id="passwordInput" class="form-control">
                    <small class="form-text text-muted">수정 시 입력하면 변경됩니다.</small>
                </div>
                <div class="form-group">
                    <label for="nameInput">이름</label>
                    <input type="text" id="nameInput" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="roleSelect">권한</label>
                    <select id="roleSelect" class="form-control">
                        <option value="ADMIN">관리자</option>
                        <option value="SUPER_ADMIN">최고 관리자</option>
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="submitAdminForm()">저장</button>
                    <button type="button" class="btn btn-secondary" onclick="closeAdminModal()">취소</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="<c:url value='/js/admin/admins.js'/>"></script>
