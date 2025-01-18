package com.qrystal.app.user.service;

import com.qrystal.app.user.model.User;
import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.dto.PasswordUpdateRequest;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserSignupRequest;
import com.qrystal.app.user.dto.UserUpdateRequest;
import com.qrystal.app.user.mapper.UserMapper;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public Long signup(UserSignupRequest request) {
        // 이메일 중복 검사
        validateDuplicateUser(request);

        // 사용자 생성
        User user = request.toEntity();
        user.setStatus(UserStatus.ACTIVE);
        user.encodePassword(passwordEncoder);
        userMapper.save(user);

        return user.getId();
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

    @Transactional
    public void updateProfile(String email, UserUpdateRequest request) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // INACTIVE 상태인 경우 수정 불가
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        user.setName(request.getName());
        userMapper.update(user);
    }

    @Transactional
    public void updatePassword(String email, PasswordUpdateRequest request) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // INACTIVE 상태인 경우 수정 불가
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // PENDING 상태인 경우 수정 불가
        if (user.getStatus() == UserStatus.PENDING) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.update(user);
    }

    @Transactional
    public void deactivateAccount(String email, String password) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 INACTIVE 상태인 경우
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // PENDING 상태인 경우 탈퇴 불가
        if (user.getStatus() == UserStatus.PENDING) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 비밀번호 확인 (소셜 로그인 사용자는 제외)
        if (user.getProvider() == null && !passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.setStatus(UserStatus.INACTIVE);
        userMapper.update(user);
    }
    public String extractEmail(Object principal) {
        log.info("Principal type: {}", principal != null ? principal.getClass().getName() : "null");
        log.info("Principal value: {}", principal);

        if (principal instanceof String) {
            return (String) principal;
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof DefaultOAuth2User) {
            Map<String, Object> attributes = ((DefaultOAuth2User) principal).getAttributes();
            log.info("OAuth2User attributes: {}", attributes);

            if (attributes.containsKey("email")) {
                return (String) attributes.get("email");
            } else if (attributes.containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return (String) response.get("email");
            } else {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            }
        }

        log.error("Principal type not supported: {}", principal != null ? principal.getClass().getName() : "null");
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    // 이메일로 사용자 찾기
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}