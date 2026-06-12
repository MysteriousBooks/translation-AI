package com.translation.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.app-expiration}")
    private long appExpiration;

    @Value("${jwt.admin-expiration}")
    private long adminExpiration;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成APP端Token
     */
    public String generateAppToken(Long userId) {
        return generateToken(userId, "app", appExpiration);
    }

    /**
     * 生成后台管理Token
     */
    public String generateAdminToken(Long adminId) {
        return generateToken(adminId, "admin", adminExpiration);
    }

    private String generateToken(Long id, String type, long expiration) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration * 1000);
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", type)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从Token获取类型(app/admin)
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    /**
     * 验证Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.warn("Token解析异常: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 获取APP端Token过期时间（秒）
     */
    public long getAppExpirationSeconds() {
        return appExpiration;
    }

    /**
     * 获取Admin端Token过期时间（秒）
     */
    public long getAdminExpirationSeconds() {
        return adminExpiration;
    }
}