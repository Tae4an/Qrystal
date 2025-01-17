package com.qrystal.app.user.dto;

import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class UserResponse {
   private Long id;
   private String email;
   private String name;
   private UserStatus status;
   private String provider;
   private String providerId;
   private String lastLoginAt;
   private String createdAt;
   private String updatedAt;

   public static UserResponse of(User user) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      return UserResponse.builder()
              .id(user.getId())
              .email(user.getEmail())
              .name(user.getName())
              .status(user.getStatus())
              .provider(user.getProvider())
              .providerId(user.getProviderId())
              .lastLoginAt(user.getLastLoginAt() != null ?
                      user.getLastLoginAt().format(formatter) : null)
              .createdAt(user.getCreatedAt().format(formatter))
              .updatedAt(user.getUpdatedAt().format(formatter))
              .build();
   }
}