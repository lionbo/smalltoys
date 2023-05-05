package com.lion.toy.controller;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorld {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(HelloWorld.class);

    @RequestMapping("/world")
    @ResponseBody
    public String helloWorld(){
        logger.info("Hello World!");
        return "Hello World!";
    }
}
