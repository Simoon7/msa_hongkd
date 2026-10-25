package com.example.msa.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// product-service의 ProductResDTO 응답을 받기 위한 DTO
// 우리가 필요한 필드만 뽑아서 정의 (전체를 다 받을 필요 없음)
@Getter
@NoArgsConstructor
public class ProductInfoDTO {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}