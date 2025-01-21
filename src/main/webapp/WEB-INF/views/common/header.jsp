<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<header>
    <nav>
        <div class="logo">
            <a href="/">Qrystal</a>
        </div>
        <ul class="nav-links">
            <sec:authorize access="isAuthenticated()">
                <li class="user-info">
                    <sec:authentication property="principal" var="principal"/>
                    <c:choose>
                        <c:when test="${not empty principal.attributes}">
                            <c:choose>
                                <c:when test="${not empty principal.attributes.name}">
                                    <span>${principal.attributes.name} 님</span>
                                </c:when>
                                <c:when test="${not empty principal.attributes.response}">
                                    <span>${principal.attributes.response.name} 님</span>
                                </c:when>
                                <c:otherwise>
                                    <span>${principal.attributes.login} 님</span>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <span>${principal.username} 님</span>
                        </c:otherwise>
                    </c:choose>
                </li>
            </sec:authorize>
            <li><a href="/questions">문제 은행</a></li>
            <sec:authorize access="isAuthenticated()">
                <li><a href="/user/profile">프로필</a></li>
                <li>
                    <form action="/auth/logout" method="post">
                        <button type="submit">로그아웃</button>
                    </form>
                </li>
            </sec:authorize>
            <sec:authorize access="!isAuthenticated()">
                <li><a href="/auth/login">로그인</a></li>
                <li><a href="/auth/signup">회원가입</a></li>
            </sec:authorize>
        </ul>
    </nav>
</header>