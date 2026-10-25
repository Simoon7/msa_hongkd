package com.example.msa.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResDTO {
    private Long id;
    private String token;
    private String refreshToken;
}