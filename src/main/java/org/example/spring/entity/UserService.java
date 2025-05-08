package org.example.spring.entity;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.PostConstruct;

@Component
public class UserService {

    @Autowired
    User user;

    @PostConstruct
    public void init() {
        System.out.println("UserService初始化，有属性"+user);
    }
}
