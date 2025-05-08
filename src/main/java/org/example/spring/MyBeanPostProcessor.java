package org.example.spring;

import org.example.spring.annotation.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object afterInitialization(Object bean, String beanName) {
        System.out.println(beanName + "初始化完成");
        return bean;
    }
}
