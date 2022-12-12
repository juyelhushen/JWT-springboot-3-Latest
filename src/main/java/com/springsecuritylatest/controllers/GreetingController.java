package com.springsecuritylatest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/")
@RestController
public class GreetingController {

    @GetMapping("greet")
    public String greet() {
        return "Hello, World!";
    }
}
