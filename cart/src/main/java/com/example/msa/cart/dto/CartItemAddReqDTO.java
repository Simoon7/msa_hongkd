package com.example.msa.cart.dto;

import lombok.Getter;

@Getter
public class CartItemAddReqDTO {
    private Long productId;
    private int quantity;
}