# 简易Spring MVC
主要流程
1. 创建`Tomcat`
2. 在`Tomcat`里注册`DispatcherServlet`，这是一个`Component`对象
3. 通过实现`BeanPostProcessor`对于所有有`@Controller`的对象进行处理，创建对应的`WebHandler`，保存对应的方法和调用对象，并被`DispatcherServlet`使用`路径-Handler`进行持有
4. `WebHandler`能够匹配相应的请求，并实现函数调用返回所需结果