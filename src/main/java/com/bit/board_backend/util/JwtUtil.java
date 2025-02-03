package com.bit.board_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "BitBoardBackEndSuperUltraStrongSecretKey";
    private final long EXP_TIME = 86400000; // 1일을 밀리초로 환산
    private final Key key;

    public JwtUtil() {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date()) // 언제 발급을 받을지 날짜 정하기
                .setExpiration(new Date(System.currentTimeMillis() + EXP_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();

        } catch (JwtException e) {
            throw new RuntimeException("JWT Token Is Not Valid");
        }
    }
}
