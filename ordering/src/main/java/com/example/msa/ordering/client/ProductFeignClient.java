package com.example.msa.ordering.client;

import com.example.msa.ordering.dto.ProductInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping("/product/{productId}")
    ProductInfoDTO getProduct(@PathVariable("productId") Long productId);
}