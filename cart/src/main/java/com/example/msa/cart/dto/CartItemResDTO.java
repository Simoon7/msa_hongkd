// cart/dto/CartItemResDTO.java
package com.example.msa.cart.dto;

import com.example.msa.cart.domain.CartItem;
import lombok.Getter;

@Getter
public class CartItemResDTO {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private int subtotal;

    // Feign으로 받아온 상품 정보(productInfo)를 같이 받아서 조립
    public CartItemResDTO(CartItem item, ProductInfoDTO productInfo) {
        this.cartItemId = item.getId();
        this.productId = item.getProductId();
        this.productName = productInfo.getName();
        this.price = productInfo.getPrice();
        this.quantity = item.getQuantity();
        this.subtotal = this.price * this.quantity;
    }
}