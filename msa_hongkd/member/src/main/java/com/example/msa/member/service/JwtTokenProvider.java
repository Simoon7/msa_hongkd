// member/service/JwtTokenProvider.java
package com.example.msa.member.service;

import com.example.msa.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RefreshScope
@Component
public class JwtTokenProvider {

    private final Key accessKey;
    private final Key refreshKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.secretKeyRt}") String secretKeyRt,
            @Value("${jwt.expirationRt}") long expirationRt) {
        this.accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyRt));
        this.accessExpirationMs = expiration * 60 * 1000;
        this.refreshExpirationMs = expirationRt * 60 * 1000;
    }

    public String generateAccessToken(Long memberId, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpirationMs))
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String email, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationMs))
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getRefreshSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}