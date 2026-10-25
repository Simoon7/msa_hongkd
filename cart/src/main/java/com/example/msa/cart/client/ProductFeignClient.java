package com.example.msa.cart.client;

import com.example.msa.cart.dto.ProductInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductFeignClient {

    @GetMapping("/product/{productId}")
    ProductInfoDTO getProduct(@PathVariable("productId") Long productId);
}