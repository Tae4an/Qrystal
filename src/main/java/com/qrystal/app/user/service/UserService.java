package com.qrystal.app.user.service;

import com.qrystal.app.user.domain.User;
import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserSignupRequest;
import com.qrystal.app.user.mapper.UserMapper;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public Long signup(UserSignupRequest request, HttpServletRequest servletRequest) {
        // 이메일 중복 검사
        validateDuplicateUser(request);

        // PENDING 상태로 사용자 생성
        User user = request.toEntity();
        user.setStatus(UserStatus.PENDING);  // 초기 상태를 PENDING으로 설정
        user.encodePassword(passwordEncoder);
        userMapper.save(user);

        // 인증 토큰 생성 및 저장
        String verificationToken = UUID.randomUUID().toString();
        emailVerificationService.saveVerificationToken(request.getEmail(), verificationToken);

        try {
            // 인증 메일 발송
            emailVerificationService.sendVerificationEmail(request.getEmail(), verificationToken);
        } catch (Exception e) {
            log.error("인증 메일 발송 실패", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

        return user.getId();
    }
    @Transactional
    public void verifyEmail(String token) {
        // 토큰으로 이메일 가져오기
        String email = emailVerificationService.getEmailByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        // 사용자 찾기
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 인증된 사용자인지 확인
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        // 사용자 상태를 ACTIVE로 변경
        user.setStatus(UserStatus.ACTIVE);
        userMapper.update(user);

        // 사용된 토큰 삭제
        emailVerificationService.deleteVerificationToken(token);
    }

    private void validateDuplicateUser(UserSignupRequest request) {
        if (userMapper.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }


    public UserResponse findByEmail(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.of(user);
    }
}