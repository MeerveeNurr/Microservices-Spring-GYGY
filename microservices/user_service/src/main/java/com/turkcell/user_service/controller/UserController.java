package com.turkcell.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/api/users")
//@GetMapping("/user/hello") ödev
    public String hello(){
        return "Hello User Service...";
    }

}
