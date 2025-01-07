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
       
       OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
       OAuth2User oAuth2User = delegate.loadUser(userRequest);

       String registrationId = userRequest.getClientRegistration().getRegistrationId();
       String userNameAttributeName = userRequest.getClientRegistration()
               .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
       
       Map<String, Object> attributes = oAuth2User.getAttributes();

       // Google 계정 정보 추출
       String email = (String) attributes.get("email");
       String name = (String) attributes.get("name");
       String provider = registrationId;
       String providerId = (String) attributes.get(userNameAttributeName);

       // DB에서 email로 기존 회원인지 확인
       Optional<User> existingUser = userMapper.findByEmail(email);

       User user;
       if (existingUser.isEmpty()) {
           // 신규 회원일 경우 회원가입 처리
           user = User.builder()
                   .email(email)
                   .password(passwordEncoder.encode("SOCIAL_" + UUID.randomUUID()))  // 임의의 비밀번호 설정
                   .name(name)
                   .provider(provider)
                   .providerId(providerId)
                   .status(UserStatus.ACTIVE)
                   .build();
           userMapper.save(user);
       } else {
           user = existingUser.get();
       }

       return new DefaultOAuth2User(
               Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
               attributes,
               userNameAttributeName
       );
   }
}