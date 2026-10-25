package com.example.msa.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateStockDTO {
    private Long productId;
    private int productQuantity;
}