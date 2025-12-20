package org.example.security;

import org.example.repository.UserRepository;
import org.example.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. 檢查 Header 是否包含 Bearer Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // 2. 從 Token 提取 email 和 role
            userEmail = jwtService.extractUsername(jwt);
            final String userRole = jwtService.extractRole(jwt);

            // 3. 如果 email 存在且 Spring Security Context 中尚未認證
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 查找資料庫，確認使用者存在
                userRepo.findByEmail(userEmail).ifPresent(user -> {
                    if (jwtService.isTokenValid(jwt, user)) {

                        // 4. 創建認證物件，將角色 (role) 加入
                        // Spring Security 角色前綴需為 "ROLE_"
                        SimpleGrantedAuthority authority =
                                new SimpleGrantedAuthority("ROLE_" + userRole);

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userEmail,
                                        null,
                                        Collections.singletonList(authority));

                        // 5. 將認證資訊設置到 Security Context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                });
            }
        } catch (Exception e) {
            // JWT 解析失敗或過期，清除 Security Context (可選：紀錄日誌)
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
