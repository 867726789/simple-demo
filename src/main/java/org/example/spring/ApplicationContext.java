package org.example.spring;

import org.example.spring.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ApplicationContext {

    private Map<String, Object> ioc = new HashMap<>();
    private Map<String, Object> loadingIoc = new HashMap<>();
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public ApplicationContext(String packageName) throws Exception {
        initContext(packageName);
    }

    public void initContext(String packageName) throws Exception {
        scanPackage(packageName).stream().filter(this::canCreated).forEach(this::wrapper);
        initBeanPostProcessor();
        beanDefinitionMap.values().forEach(this::createBean);
    }

    private void initBeanPostProcessor() {
        beanDefinitionMap.values().stream()
                .filter(bd->BeanPostProcessor.class.isAssignableFrom(bd.getBeanType()))
                .map(this::createBean)
                .map(bean->(BeanPostProcessor) bean)
                .forEach(beanPostProcessors::add);
    }

    protected Object createBean(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();
        if (ioc.containsKey(name)) {
            return ioc.get(name);
        }
        if (loadingIoc.containsKey(name)) {
            return loadingIoc.get(name);
        }
        return doCreateBean(beanDefinition);
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getConstructor();
        Object bean = null;
        try {
            bean = constructor.newInstance();
            // 半成品
            loadingIoc.put(beanDefinition.getName(), bean);
            // 属性注入
            autowiredBean(bean,beanDefinition);
            // 初始化方法 + 后置处理器
            bean = initializeBean(bean,beanDefinition);

            loadingIoc.remove(beanDefinition.getName());
            // 完全体
            ioc.put(beanDefinition.getName(), bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bean;
    }

    private Object initializeBean(Object bean, BeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        for (BeanPostProcessor postProcessor : beanPostProcessors) {
            bean = postProcessor.beforeInitialization(bean,beanDefinition.getName());
        }

        Method postConstruct = beanDefinition.getPostConstructMethod();
        if (postConstruct != null) {
            postConstruct.invoke(bean);
        }

        for (BeanPostProcessor postProcessor : beanPostProcessors) {
            bean = postProcessor.afterInitialization(bean,beanDefinition.getName());
        }
        return bean;
    }

    private void autowiredBean(Object bean, BeanDefinition beanDefinition) throws IllegalAccessException {
        for (Field field : beanDefinition.getAutowiredFields()) {
            field.setAccessible(true);
            field.set(bean,getBean(field.getType()));
        }
    }

    protected boolean canCreated(Class<?> type) {
        return type.isAnnotationPresent(Component.class);
    }

    protected BeanDefinition wrapper(Class<?> type) {
        BeanDefinition beanDefinition = new BeanDefinition(type);
        if (beanDefinitionMap.containsKey(beanDefinition.getName())) {
            throw  new RuntimeException("Bean已经存在");
        }
        beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);
        return beanDefinition;
    }

    private List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> classList = new ArrayList<>();
        URL resource = this.getClass().getClassLoader().getResource(packageName.replace(".", "/"));
        Path path = null;
        path = new File(resource.toURI()).toPath();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path absolutePath = file.toAbsolutePath();
                if (absolutePath.getFileName().toString().endsWith(".class")) {
                    String replaceStr = absolutePath.toString().replace("\\", ".").replace(".class", "");
                    int packageIndex = replaceStr.indexOf(packageName);
                    String className = replaceStr.substring(packageIndex);
//                    System.out.println(className);
                    try {
                        classList.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classList;
    }

    public Object getBean(String beanName) {
        if (beanName == null) {
            return null;
        }
        Object bean = ioc.get(beanName);
        if (bean != null) {
            return bean;
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            return createBean(beanDefinitionMap.get(beanName));
        }
        return null;
    }

    public <T> T getBean(Class<T> beanType) {
        String beanName = this.beanDefinitionMap.values().stream()
                .filter(bd -> beanType.isAssignableFrom(bd.getBeanType()))
                .map(BeanDefinition::getName)
                .findFirst().orElse(null);
        return (T) getBean(beanName);
    }

    public <T> List<T> getBeans(Class<T> beanType) {
        return  this.beanDefinitionMap.values().stream()
                .filter(bd -> beanType.isAssignableFrom(bd.getBeanType()))
                .map(BeanDefinition::getName)
                .map(this::getBean)
                .map(bean->(T) bean)
                .toList();

    }
}
