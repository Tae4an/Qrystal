package com.qrystal.app.admin.security;

import com.qrystal.app.admin.domain.AdminStatus;
import com.qrystal.app.admin.mapper.AdminMapper;
import com.qrystal.app.admin.model.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {
   private final AdminMapper adminMapper;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Admin admin = adminMapper.findByAdminId(username)
               .orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

       if (admin.getStatus() == AdminStatus.INACTIVE) {
           throw new LockedException("비활성화된 계정입니다.");
       }

       return new AdminDetails(admin);
   }
}