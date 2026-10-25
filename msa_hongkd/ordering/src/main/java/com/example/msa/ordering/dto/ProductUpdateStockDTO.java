package com.example.msa.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUpdateStockDTO {
    private Long productId;
    private int productQuantity;
}