package com.qrystal.util;

import com.qrystal.app.admin.dto.AdminResponse;
import com.qrystal.app.admin.security.AdminDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {
    
    /**
     * 현재 인증된 관리자의 정보를 가져오는 메서드
     * 
     * @return AdminResponse 객체로 변환된 현재 인증된 관리자 정보
     */
    public static AdminResponse getCurrentAdmin() {
        // SecurityContextHolder에서 현재 인증 정보를 가져옴
        AdminDetails adminDetails = (AdminDetails) SecurityContextHolder
            .getContext() // 현재 보안 컨텍스트 가져오기
            .getAuthentication() // 현재 인증 객체 가져오기
            .getPrincipal(); // 인증된 주체(Principal) 가져오기
            
        // AdminDetails 객체에서 Admin 정보를 추출하여 AdminResponse 객체로 변환
        return AdminResponse.of(adminDetails.getAdmin());
    }
}