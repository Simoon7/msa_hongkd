package com.example.msa.ordering.dto;

import com.example.msa.ordering.domain.Ordering;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResDTO {
    private Long orderId;
    private Long productId;
    private int quantity;
    private String orderStatus;
    private LocalDateTime createdTime;

    public OrderResDTO(Ordering ordering) {
        this.orderId = ordering.getId();
        this.productId = ordering.getProductId();
        this.quantity = ordering.getQuantity();
        this.orderStatus = ordering.getOrderStatus().name();
        this.createdTime = ordering.getCreatedTime();
    }
}