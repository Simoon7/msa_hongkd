// member/service/InitialDataLoader.java
package com.example.msa.member.service;

import com.example.msa.member.domain.Member;
import com.example.msa.member.domain.Role;
import com.example.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@naver.com";

        if (!memberRepository.existsByEmail(adminEmail)) {
            Member admin = Member.builder()
                    .name("admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("12341234"))
                    .role(Role.ADMIN)
                    .build();
            memberRepository.save(admin);
        }
    }
}