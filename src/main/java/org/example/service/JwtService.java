package org.example.service;

import org.example.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // 移除原本的 private static final String SECRET_KEY = ...;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey; // 🎯 新增：使用 @Value 注入

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // 產生 JWT Token
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // 核心：將角色放入 Token 內
        // 📢 請加上這行來確認數值
        System.out.println("DEBUG JWT Expiration (ms): " + jwtExpiration);
        claims.put("role", user.getRole());
        return buildToken(claims, user.getEmail(), jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject) // sub: email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 從 Token 提取 email (subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從 Token 提取 role (custom claim)
    public String extractRole(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    // 驗證 Token 是否有效
// 🎯 修復：將參數型別從 User 改為 Spring Security 的 UserDetails 介面
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            // 這裡檢查名字是否匹配，且沒有過期
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            // 🌟 關鍵：如果解析過程噴出任何異常（如過期、簽名錯誤），代表 Token 無效
            return false;
        }
    }

    // 檢查 Token 是否過期
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 輔助方法：提取 Claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            // 如果在提取過程中出錯（例如 Token 已過期導致 extractAllClaims 崩潰）
            // 拋出一個自定義或原有的異常，或者讓上層處理
            throw e;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 獲取簽名 Key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
