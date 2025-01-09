package com.qrystal.app.user.mapper;

import com.qrystal.app.user.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    void save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void update(User user);
    void delete(Long id);
}