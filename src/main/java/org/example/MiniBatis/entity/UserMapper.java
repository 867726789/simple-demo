package org.example.MiniBatis.entity;

import org.example.MiniBatis.annotation.Param;

import java.util.List;

public interface UserMapper {

    public User selectById(@Param("id") int id);

    public User selectByName(@Param("name") String name);

    public List<User> selectByAge(@Param("age") Integer age);
}
