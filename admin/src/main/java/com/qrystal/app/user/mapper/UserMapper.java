package com.qrystal.app.user.mapper;

import com.qrystal.app.user.dto.UserSearchCondition;
import com.qrystal.app.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    List<User> findAllWithCondition(
            @Param("condition") UserSearchCondition condition,
            @Param("offset") int offset,
            @Param("size") int size);

    int countByCondition(@Param("condition") UserSearchCondition condition);
    void update(User user);

    Optional<User> findById(Long id);
}