package com.example.msa.cart.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CartResDTO {
    private Long cartId;
    private List<CartItemResDTO> items;
    private int totalPrice;

    public CartResDTO(Long cartId, List<CartItemResDTO> items) {
        this.cartId = cartId;
        this.items = items;
        this.totalPrice = items.stream().mapToInt(CartItemResDTO::getSubtotal).sum();
    }
}