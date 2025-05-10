# 简易Spring
主要流程如下：
1. 通过`pakageName`扫描对应的包下所有的文件，通过自定义的`canCreated`方法进行需要的类的过滤比如`@Component`对应的类
2. 获取所有需要管理的类之后，创建其对应的`BeanDefinition`保存必要的属性供之后使用，如Bean的名字，类型，构造函数和初始化方法
3. 先实例化`BeanPostProcessor`可以用于处理其他类
4. 实例化其他类
5. 通过三（两）层缓存来解决循环依赖，提前暴露未完成的`Bean`，

## 注意
>通过类型获取`Bean`为什么不使用`Map`?
> 
> 因为类型应当是能够被赋值，即为子类就能注入，因此采用`isAssignableFrom`更好