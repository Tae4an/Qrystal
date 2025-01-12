package com.qrystal.config;

import com.qrystal.app.admin.security.AdminDetailsService;
import com.qrystal.app.admin.security.handler.LoginFailureHandler;
import com.qrystal.app.admin.security.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
   private final AdminDetailsService adminDetailsService;
   private final LoginSuccessHandler loginSuccessHandler;
   private final LoginFailureHandler loginFailureHandler;

   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
           .csrf().disable()
           .headers().frameOptions().sameOrigin()
           .and()
           .authorizeRequests()
               .antMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
               .antMatchers("/login", "/error").permitAll()
               .anyRequest().authenticated()
           .and()
               .formLogin()
               .loginPage("/login")
               .loginProcessingUrl("/login")
               .usernameParameter("adminId")
               .passwordParameter("password")
               .successHandler(loginSuccessHandler)
               .failureHandler(loginFailureHandler)
           .and()
               .logout()
               .logoutUrl("/logout")
               .logoutSuccessUrl("/login")
               .invalidateHttpSession(true)
               .clearAuthentication(true)
           .and()
               .sessionManagement()
               .maximumSessions(1)
               .maxSessionsPreventsLogin(true);

       http.userDetailsService(adminDetailsService);

       return http.build();
   }
}