package org.example.spring.web;

import lombok.Getter;
import org.apache.ibatis.annotations.ResultType;
import org.example.spring.web.annotation.ResponseBody;

import java.lang.reflect.Method;

@Getter
public class WebHandler {

    private final Object controllerBean;

    private final Method method;

    private final ResultType resultType;

    public WebHandler(Object controllerBean, Method method) {
        this.controllerBean = controllerBean;
        this.method = method;
        this.resultType = resolveResultType(controllerBean,method);
    }

    private ResultType resolveResultType(Object controllerBean, Method method) {
        if (method.isAnnotationPresent(ResponseBody.class)) {
            return ResultType.JSON;
        }
        if (method.getReturnType() == ModelAndView.class){
            return ResultType.LOCAL;
        }
        return ResultType.HTML;
    }


    enum ResultType {
        JSON,HTML,LOCAL
    }
}
