package com.qrystal.app.user.service;

import com.qrystal.app.user.domain.User;
import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        // OAuth2UserService 객체 생성 및 OAuth2User 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 제공자(google, github) 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2User의 속성값 Map
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2User attributes: {}", attributes);  // 받아온 정보 로깅

        // 소셜 로그인 제공자에 따라 유저 정보를 가져옴
        String email;
        String name;
        String providerId;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            providerId = (String) attributes.get(userNameAttributeName);
        } else if ("github".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            // GitHub은 이메일이 private일 수 있음
            if (email == null) {
                email = (String) attributes.get("login") + "@github.com";
            }
            // GitHub은 이름을 설정하지 않았을 수 있음
            if (name == null) {
                name = (String) attributes.get("login");
            }
            // GitHub의 경우 ID가 숫자로 오므로 String으로 변환
            providerId = attributes.get(userNameAttributeName).toString();
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        String provider = registrationId;

        // DB에서 email로 기존 회원인지 확인
        Optional<User> existingUser = userMapper.findByEmail(email);

        User user;
        if (existingUser.isEmpty()) {
            // 신규 회원일 경우 회원가입 처리
            user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("SOCIAL_" + UUID.randomUUID())) // 임의의 비밀번호 설정
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .status(UserStatus.ACTIVE)
                    .build();
            userMapper.save(user);
            log.info("소셜 로그인 회원가입 완료 - email: {}, provider: {}", email, provider);
        } else {
            user = existingUser.get();
            log.info("기존 회원 소셜 로그인 - email: {}, provider: {}", email, provider);
        }

        // 인증 정보 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName
        );
    }
}