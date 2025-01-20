package com.qrystal.app.user.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (isAjaxRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
        } else {
            // 알림 메시지를 세션에 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginMessage", "로그인이 필요한 서비스입니다. 로그인 후 이용해 주세요.");
            
            // 현재 요청 URL을 세션에 저장 (로그인 후 원래 페이지로 돌아가기 위함)
            String targetUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                targetUrl += "?" + queryString;
            }
            session.setAttribute("targetUrl", targetUrl);
            
            // 로그인 페이지로 리다이렉트
            response.sendRedirect("/auth/login");
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}