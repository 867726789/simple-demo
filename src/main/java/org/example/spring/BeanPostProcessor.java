package org.example.spring;

import org.springframework.beans.BeansException;

public interface BeanPostProcessor {

    default Object beforeInitialization(Object bean, String beanName) {
        return bean;
    };

    default Object afterInitialization(Object bean, String beanName) {
        return bean;
    };

}
