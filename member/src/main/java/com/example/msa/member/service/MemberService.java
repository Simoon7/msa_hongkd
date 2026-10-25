package com.example.msa.member.service;

import com.example.msa.member.domain.Member;
import com.example.msa.member.dto.*;
import com.example.msa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 200;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public Long register(MemberSaveReqDTO dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        Member member = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        return memberRepository.save(member).getId();
    }

    public MemberLoginResDTO login(MemberLoginReqDTO dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail(), member.getRole());
        redisTemplate.opsForValue().set(member.getEmail(), refreshToken, REFRESH_TOKEN_TTL_DAYS, TimeUnit.DAYS);

        return new MemberLoginResDTO(member.getId(), accessToken, refreshToken);
    }

    public String reissueAccessToken(MemberRefreshReqDTO dto) {
        String requestToken = dto.getRefreshToken();
        if (!jwtTokenProvider.validateRefreshToken(requestToken)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        String email = jwtTokenProvider.getRefreshSubject(requestToken);
        String savedToken = redisTemplate.opsForValue().get(email);

        if (savedToken == null || !savedToken.equals(requestToken)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return jwtTokenProvider.generateAccessToken(member.getId(), member.getRole());
    }
}