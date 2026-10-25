package com.example.msa.ordering.dto;

import lombok.Getter;

@Getter
public class OrderCreateReqDTO {
    private Long productId;
    private int quantity;
}