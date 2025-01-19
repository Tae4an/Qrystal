package com.qrystal.app.user.dto;

import com.qrystal.app.user.model.User;
import com.qrystal.app.user.domain.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private String provider;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 일반 사용자 여부 확인
    public boolean getIsLocalUser() {
        return provider == null || provider.isEmpty();
    }

    // provider 관련 메서드들
    public String getProviderType() {
        return getIsLocalUser() ? "email" : provider.toLowerCase();
    }

    public String getProviderDisplayName() {
        return getIsLocalUser() ? "이메일" : provider;
    }

    public String getProviderIconClass() {
        return getIsLocalUser() ? "fa-envelope" : "fa-" + provider.toLowerCase();
    }

    // 날짜 포맷팅을 위한 메서드
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return createdAt.format(formatter);
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}