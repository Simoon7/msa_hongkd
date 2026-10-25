// product/dto/ProductResDTO.java
package com.example.msa.product.dto;

import com.example.msa.product.domain.Product;
import lombok.Getter;

@Getter
public class ProductResDTO {
    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String description;
    private Long memberId;

    public ProductResDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.description = product.getDescription();
        this.memberId = product.getMemberId();
    }
}