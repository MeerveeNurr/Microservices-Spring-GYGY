package com.turkcell.cart_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {
    @GetMapping("/cart/hello")
    public String hello(){
        return "Hello Cart Service...";
    }

}
