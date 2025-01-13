<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="header">
    <div class="header-left">
        <button id="sidebar-toggle" class="sidebar-toggle">
            <i class="fas fa-bars"></i>
        </button>
    </div>
    <div class="header-right">
        <span class="admin-info">
            <i class="fas fa-user"></i> ${admin.name}
        </span>
        <a href="/logout" class="btn-logout">
            <i class="fas fa-sign-out-alt"></i> 로그아웃
        </a>
    </div>
</header>