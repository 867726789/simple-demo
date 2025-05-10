package org.example.spring.web;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.spring.BeanPostProcessor;
import org.example.spring.annotation.Component;
import org.example.spring.web.annotation.Controller;
import org.example.spring.web.annotation.Param;
import org.example.spring.web.annotation.RequestMapping;
import org.springframework.beans.BeansException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DispatcherServlet extends HttpServlet implements BeanPostProcessor {

    private static final Pattern PATTERN = Pattern.compile("#\\{(.*?)}");

    private Map<String,WebHandler> handlerMap = new HashMap<>();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        WebHandler webHandler = findController(req);
        if (webHandler == null) {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("<h1>Error 请求资源不存在 <h1> <br>" );
            return;
        }
        try {
            Object controllerBean = webHandler.getControllerBean();
            Method method = webHandler.getMethod();
            Object[] args = resolveArgs(method,req);
            Object result = method.invoke(controllerBean,args);
            switch (webHandler.getResultType()) {
                case HTML -> {
                    resp.setContentType("text/html;charset=utf-8");
                    resp.getWriter().write(result.toString());
                }
                case JSON -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.getWriter().write(JSON.toJSONString(result));
                }
                case LOCAL -> {
                    ModelAndView mv = (ModelAndView) result;
                    String view = mv.getView();
                    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(view);
                    try(resourceAsStream) {
                        String html = new String(resourceAsStream.readAllBytes());
                        html = renderTemplate(html,mv.getContext());
                        resp.setContentType("text/html;charset=utf-8");
                        resp.getWriter().write(html);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    private String renderTemplate(String template, Map<String, String> context) {
        Matcher matcher = PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = context.getOrDefault(key,"");
            matcher.appendReplacement(sb,Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Object[] resolveArgs(Method method, HttpServletRequest req) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String value = null;
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                value = req.getParameter(param.value());
            } else {
                value = req.getParameter(parameter.getName());
            }
            Class<?> parameterType = parameter.getType();
            if (String.class.isAssignableFrom(parameterType)) {
                args[i] = value;
            } else if (Integer.class.isAssignableFrom(parameterType)) {
                args[i] = Integer.parseInt(value);
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    private WebHandler findController(HttpServletRequest req) {
        return handlerMap.get(req.getRequestURI());
    }

    @Override
    public Object afterInitialization(Object bean, String beanName) throws BeansException {
        if (!bean.getClass().isAnnotationPresent(Controller.class)) {
            return bean;
        }
        RequestMapping classRm = bean.getClass().getAnnotation(RequestMapping.class);
        final String classUrl = classRm == null ? "" : classRm.value();
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(m->m.isAnnotationPresent(RequestMapping.class))
                .forEach(m->{
                    RequestMapping methodRm = m.getAnnotation(RequestMapping.class);
                    String key = classUrl.concat(methodRm.value());
                    WebHandler webHandler = new WebHandler(bean,m);
                    if (handlerMap.put(key,webHandler) != null) {
                        throw new RuntimeException("controller定义重复: " + key);
                    }
                });
        return bean;
    }
}
