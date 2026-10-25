// product/dto/ProductUpdateReqDTO.java
package com.example.msa.product.dto;

import lombok.Getter;

@Getter
public class ProductUpdateReqDTO {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String description;
}