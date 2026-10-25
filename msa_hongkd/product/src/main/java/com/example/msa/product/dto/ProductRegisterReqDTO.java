package com.example.msa.product.dto;

import com.example.msa.product.domain.Product;
import lombok.Getter;

@Getter
public class ProductRegisterReqDTO {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String description;

    public Product toEntity(Long memberId) {
        return Product.builder()
                .name(name)
                .category(category)
                .price(price)
                .stockQuantity(stockQuantity)
                .description(description)
                .memberId(memberId)
                .build();
    }
}