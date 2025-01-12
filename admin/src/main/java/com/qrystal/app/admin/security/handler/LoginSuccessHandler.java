package com.qrystal.app.admin.security.handler;

import com.qrystal.app.admin.mapper.AdminMapper;
import com.qrystal.app.admin.model.Admin;
import com.qrystal.app.admin.security.AdminDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AdminMapper adminMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        Admin admin = adminDetails.getAdmin();

        adminMapper.updateLastLogin(admin.getId(), LocalDateTime.now());
        log.info("관리자 로그인 성공: {}", admin.getAdminId());

        response.sendRedirect("/");
    }
}
