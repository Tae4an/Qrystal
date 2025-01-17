package com.qrystal.app.user.service;

import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserSearchCondition;
import com.qrystal.app.user.mapper.UserMapper;
import com.qrystal.app.user.model.User;
import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.qrystal.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
   private final UserMapper userMapper;

   public Page<UserResponse> getUsers(UserSearchCondition condition, PageRequest pageRequest) {
       // 전체 데이터 수 조회
       int total = userMapper.countByCondition(condition);
       
       // 데이터가 없는 경우 빈 페이지 반환
       if (total == 0) {
           return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
       }

       // 조건에 맞는 사용자 목록 조회
       List<User> users = userMapper.findAllWithCondition(
           condition,
               (int) pageRequest.getOffset(),
           pageRequest.getPageSize()
       );

       // DTO 변환
       List<UserResponse> userResponses = users.stream()
               .map(UserResponse::of)
               .collect(Collectors.toList());

       return new PageImpl<>(userResponses, pageRequest, total);
   }

   public UserResponse getUserDetail(Long id) {
       User user = userMapper.findById(id)
               .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

       return UserResponse.of(user);
   }

   @Transactional
   public void updateUserStatus(Long id, UserStatus status) {
       User user = userMapper.findById(id)
               .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

       // 상태 업데이트 로직
       User updatedUser = User.builder()
               .id(id)
               .email(user.getEmail())
               .name(user.getName())
               .status(status)
               .provider(user.getProvider())
               .providerId(user.getProviderId())
               .lastLoginAt(user.getLastLoginAt())
               .createdAt(user.getCreatedAt())
               .updatedAt(LocalDateTime.now())
               .build();

       userMapper.update(updatedUser);
   }
}
