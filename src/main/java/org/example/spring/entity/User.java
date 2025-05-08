package org.example.spring.entity;

import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.PostConstruct;

@Component
public class User {

    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        System.out.println("User初始化，有属性"+userService);
    }
}
