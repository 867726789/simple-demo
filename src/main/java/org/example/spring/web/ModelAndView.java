package org.example.spring.web;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ModelAndView {

    private String view;

    private Map<String, String> context = new HashMap<>();

}
