package com.xingzhi.shortvideosharingplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成JWT
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 将字符串密钥转换为安全的SecretKey
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .setExpiration(new Date(System.currentTimeMillis() + ttlMillis))
                .compact();
    }

    /**
     * 解析JWT
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T getClaimsAttribute(HttpServletRequest request, String tokenName, String secretKey, String key, Class<T> clazz) {
        String token = request.getHeader(tokenName);
        Claims claims = JwtUtil.parseJWT(secretKey, token);
        return claims.get(key, clazz);

    }
}
