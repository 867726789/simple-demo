package org.example.spring.web.controller;

import org.example.spring.annotation.Component;
import org.example.spring.web.ModelAndView;
import org.example.spring.web.User;
import org.example.spring.web.annotation.Controller;
import org.example.spring.web.annotation.Param;
import org.example.spring.web.annotation.RequestMapping;
import org.example.spring.web.annotation.ResponseBody;

@Controller
@Component
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/html")
    public String html(@Param("name") String name, @Param("age") Integer age) {
        return String.format("<h1> Hello World<h1><br> name: %s   age:%s", name,age);
    }

    @RequestMapping("/json")
    @ResponseBody
    public User json(@Param("name") String name, @Param("age") Integer age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        return user;
    }

    @RequestMapping("/local")
    public ModelAndView local(@Param("name") String name, @Param("age") Integer age) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("index.html");
        modelAndView.getContext().put("name",name);
        modelAndView.getContext().put("age",age.toString());
        return modelAndView;
    }
}
