package com.qrystal.app.user.dto;

import com.qrystal.app.user.domain.UserStatus;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatusUpdateRequest {
    @NotNull(message = "상태는 필수입니다")
    private UserStatus status;
}