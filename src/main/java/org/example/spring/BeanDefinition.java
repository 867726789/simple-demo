package org.example.spring;

import lombok.Getter;
import org.example.MiniBatis.annotation.Param;
import org.example.spring.annotation.Autowired;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.PostConstruct;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Getter
public class BeanDefinition {

    private final String name;
    private final Class<?> beanType;
    private final Constructor<?> constructor;
    private final Method postConstructMethod;
    private final List<Field> autowiredFields;

    public BeanDefinition(Class<?> type) {
        this.beanType = type;
        Component component = type.getDeclaredAnnotation(Component.class);
        this.name = component.name().isEmpty() ? type.getSimpleName() : component.name();
        System.out.println(this.name);
        try {
            this.constructor = type.getConstructor();
            this.postConstructMethod = Arrays.stream(type.getDeclaredMethods()).filter(m->m.isAnnotationPresent(PostConstruct.class)).findFirst().orElse(null);
            this.autowiredFields =  Arrays.stream(type.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Autowired.class)).toList();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
