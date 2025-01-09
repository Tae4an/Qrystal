package com.qrystal.app.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordUpdateRequest {
    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다")
    private String currentPassword;
    
    @NotBlank(message = "새 비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;
    
    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다")
    private String confirmPassword;
}