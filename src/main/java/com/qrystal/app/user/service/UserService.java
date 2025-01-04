package com.qrystal.app.user.service;

import com.qrystal.app.user.domain.User;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long signup(UserSignupRequest request, HttpServletRequest servletRequest) {
        validateDuplicateUser(request);

        User user = request.toEntity();
        user.encodePassword(passwordEncoder);
        userMapper.save(user);

        // 회원가입 후 자동 로그인 처리
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("자동 로그인 실패", e);
        }

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
}