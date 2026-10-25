package com.example.msa.member.dto;

import com.example.msa.member.domain.Member;
import lombok.Getter;

@Getter
public class MemberSaveReqDTO {
    private String name;
    private String email;
    private String password;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .build();
    }
}