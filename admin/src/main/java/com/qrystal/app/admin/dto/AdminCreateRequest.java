package com.qrystal.app.admin.dto;

import com.qrystal.app.admin.domain.AdminRole;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminCreateRequest {
    @NotBlank(message = "관리자 ID는 필수입니다")
    private String adminId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    private AdminRole role;
}