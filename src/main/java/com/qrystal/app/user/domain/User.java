package com.qrystal.app.user.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}