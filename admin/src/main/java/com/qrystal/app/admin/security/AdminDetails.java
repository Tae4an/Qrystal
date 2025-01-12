package com.qrystal.app.admin.security;

import com.qrystal.app.admin.domain.AdminStatus;
import com.qrystal.app.admin.model.Admin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AdminDetails implements UserDetails {
   private final Admin admin;

   public AdminDetails(Admin admin) {
       this.admin = admin;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()));
   }

   @Override
   public String getPassword() {
       return admin.getPassword();
   }

   @Override
   public String getUsername() {
       return admin.getAdminId();
   }

   @Override
   public boolean isAccountNonExpired() {
       return true;
   }

   @Override
   public boolean isAccountNonLocked() {
       return admin.getStatus() == AdminStatus.ACTIVE;
   }
   @Override
   public boolean isCredentialsNonExpired() {
       return true;
   }

   @Override
   public boolean isEnabled() {
       return admin.getStatus() == AdminStatus.ACTIVE;
   }
}