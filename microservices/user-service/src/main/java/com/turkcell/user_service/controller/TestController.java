package com.turkcell.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkcell.user_service.client.ProductServiceClient;
import com.turkcell.user_service.client.model.ProductTestResponse;

@RequestMapping("/api/test")
@RestController
public class TestController {
    private final ProductServiceClient productServiceClient;

    public TestController(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }
    
  @GetMapping
   public String test(){
    // product-service'e git. /api/products/test endpoint'ine istek at. Gelen cevabı döndür.
    ProductTestResponse response = productServiceClient.test();
    return response.message();
   }
}
