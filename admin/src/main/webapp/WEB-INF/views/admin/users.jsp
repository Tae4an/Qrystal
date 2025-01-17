<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/admin/users.css'/>">

<div class="users">
    <div class="users-header">
        <h2>사용자 관리</h2>
        <form id="searchForm" method="get" action="/users" class="users-filters">
            <div class="filter-group">
                <select name="status" class="form-control" onchange="this.form.submit()">
                    <option value="">전체 상태</option>
                    <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>활성</option>
                    <option value="INACTIVE" ${status == 'INACTIVE' ? 'selected' : ''}>비활성</option>
                </select>

                <select name="type" class="form-control" onchange="this.form.submit()">
                    <option value="">전체 유형</option>
                    <option value="DEFAULT" ${type == 'DEFAULT' ? 'selected' : ''}>일반</option>
                    <option value="SOCIAL" ${type == 'SOCIAL' ? 'selected' : ''}>소셜</option>
                </select>

                <div class="search-box">
                    <input type="text" name="search" value="${search}"
                           class="form-control" placeholder="이메일 또는 이름으로 검색">
                    <button type="submit" class="btn btn-icon">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
        </form>
    </div>
    <div class="users-content">
        <table class="table">
            <thead>
            <tr>
                <th>이메일</th>
                <th>이름</th>
                <th>가입유형</th>
                <th>상태</th>
                <th>마지막 로그인</th>
                <th>가입일</th>
                <th>작업</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${users.content}" var="user">
                <tr>
                    <td>${user.email}</td>
                    <td>${user.name}</td>
                    <td>
                            <span class="badge ${user.provider == null ? 'badge-default' : 'badge-social'}">
                                    ${user.provider == null ? '일반' : '소셜'}
                            </span>
                    </td>
                    <td>
                            <span class="badge ${user.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">
                                    ${user.status == 'ACTIVE' ? '활성' : '비활성'}
                            </span>
                    </td>
                    <td>${user.lastLoginAt}</td>
                    <td>${user.createdAt}</td>
                    <td>
                        <div class="btn-group">
                            <button type="button"
                                    class="btn btn-sm ${user.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}"
                                    onclick="updateUserStatus(${user.id}, '${user.status == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'}')">
                                    ${user.status == 'ACTIVE' ? '비활성화' : '활성화'}
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination">
        <c:forEach begin="1" end="${users.totalPages}" var="i">
            <a href="/users?page=${i-1}&size=${users.size}&search=${search}&status=${status}&type=${type}"
               class="btn ${users.number == i-1 ? 'active' : ''}">
                    ${i}
            </a>
        </c:forEach>
    </div>
</div>

<script src="<c:url value='/js/admin/users.js'/>"></script>