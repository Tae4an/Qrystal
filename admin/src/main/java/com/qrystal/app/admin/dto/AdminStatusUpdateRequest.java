package com.qrystal.app.admin.dto;

import com.qrystal.app.admin.domain.AdminStatus;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminStatusUpdateRequest {
    @NotNull(message = "상태는 필수입니다")
    private AdminStatus status;
}
