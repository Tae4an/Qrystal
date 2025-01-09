package com.qrystal.app.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;
}
