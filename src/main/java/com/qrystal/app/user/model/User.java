package com.qrystal.app.user.model;

import com.qrystal.app.user.domain.UserStatus;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserStatus status;
    private String provider;        // GOOGLE, KAKAO 등 소셜 로그인 제공자
    private String providerId;      // 소셜 로그인 식별자
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}