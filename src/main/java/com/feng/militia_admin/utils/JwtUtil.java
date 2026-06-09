package com.feng.militia_admin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_PERMISSIONS = "permissions";

    /**
     * 生成包含用户ID、角色和权限信息的 Token
     */
    public String generateToken(Long userId, String username, String role, List<String> permissions) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_PERMISSIONS, permissions != null ? permissions : new ArrayList<>())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 从token中获取用户ID
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            Object userId = claims.get(CLAIM_USER_ID);
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    // 从token中获取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    // 从token中获取角色
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? (String) claims.get(CLAIM_ROLE) : null;
    }

    // 从token中获取权限列表
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return new ArrayList<>();
        }
        Object perms = claims.get(CLAIM_PERMISSIONS);
        if (perms instanceof List) {
            return (List<String>) perms;
        }
        return new ArrayList<>();
    }

    // 验证token是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 获取过期时间
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    private Claims getClaims(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
