package com.example.msa.ordering.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInfoDTO {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}