package com.qrystal.app.admin.dto;

import com.qrystal.app.admin.domain.AdminRole;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminUpdateRequest {
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    private AdminRole role;
    private String password;  // 선택적 비밀번호 변경
}