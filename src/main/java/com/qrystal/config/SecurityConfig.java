package com.qrystal.config;

import com.qrystal.app.user.service.AuthService;
import com.qrystal.app.user.service.CustomAuthenticationEntryPoint;
import com.qrystal.app.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthService authService) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                    // 정적 리소스 및 기본 페이지
                    .antMatchers("/", "/auth/**", "/js/**", "/css/**", "/img/**", "/favicon.ico").permitAll()
                    // 문제 조회/풀이 관련 (누구나 접근 가능)
                    .antMatchers(HttpMethod.GET, "/questions", "/api/questions/**").permitAll()
                    .antMatchers("/api/categories").permitAll()
                    // 그 외 모든 요청은 인증 필요
                   .anyRequest().authenticated()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .formLogin()
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/auth/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/auth/login?error=true")
                .and()
                    .oauth2Login()
                    .loginPage("/auth/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/auth/login?error=true")
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                .and()
                .and()
                    .logout()
                    .logoutUrl("/auth/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true);

        http.userDetailsService(authService);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}