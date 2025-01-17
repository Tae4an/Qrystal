package com.qrystal.app.user.model;

import com.qrystal.app.user.domain.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String name;
    private UserStatus status;
    private String provider;
    private String providerId;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}