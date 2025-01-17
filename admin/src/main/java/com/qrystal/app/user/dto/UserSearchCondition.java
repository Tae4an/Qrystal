package com.qrystal.app.user.dto;

import com.qrystal.app.user.domain.UserStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSearchCondition {
    private String search;
    private UserStatus status;
    private String type;  // "DEFAULT" or "SOCIAL"
}