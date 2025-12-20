package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 最終配置類
 * 採用標準的 SecurityFilterChain 進行授權和 JWT 過濾器的配置，並使用 CorsConfigurationSource 處理 CORS。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // 🚨 修正：刪除 WebSecurityCustomizer bean，不再使用 web.ignoring() 繞過安全鏈
    // 🚨 修正：刪除 FilterRegistrationBean bean，不再使用 Order 0 的 CorsFilter

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 啟用 CORS (使用下面的 CorsConfigurationSource bean)
                .cors(Customizer.withDefaults())

                // 2. 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3. 設置 API 權限規則 (授權配置)
                .authorizeHttpRequests(auth -> auth
                        // 1. OPTIONS 請求必須最優先放行
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. 公開路徑
                        .requestMatchers("/api/public/**").permitAll() // 涵蓋 /api/public/books/**
                        .requestMatchers("/api/auth/**").permitAll()

                        // 3. ADMIN 權限路徑
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // 🎯 簡化 E: 需登入用戶才能訪問的路徑 (涵蓋 /api/user/cart, /api/user/orders 等)
                        // 刪除 /api/cart/** 和 /api/orders/**，因為它們都被 /api/user/** 涵蓋
                        .requestMatchers("/api/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER", "ROLE_USER")

                        // 4. 其他所有路徑都需要認證（作為最終的防線）
                        .anyRequest().authenticated()
                )

                // 4. 設置會話管理為無狀態 (JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. 設置身份驗證提供者
                .authenticationProvider(authenticationProvider)

                // 6. 添加 JWT 過濾器 (在 UsernamePasswordAuthenticationFilter 之前執行)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 設置 CORS 策略：由 Spring Security 框架自動應用到 Filter Chain 中。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🚨 修正 1: 允許多個來源 (本地測試 + Vercel 雲端)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173", // Vite 預設
                "http://localhost:5174", // 你的本地開發埠
                "https://foreign-languages-book.vercel.app", // 🌟 填入你剛才產出的 Vercel 網址
                "https://foreign-languages-book-git-master-chrischou19990904s-projects.vercel.app" // 建議也加上這個預覽網址
        ));

        // 🚨 修正 2: 允許的方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 🚨 修正 3: 允許的 Header
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        // 🚨 修正 4: 很重要！你原本的 code 最後一行又把 AllowCredentials 設為 false，會蓋掉前面的設定
        // 如果前端 Axios 有設定 withCredentials: true，這裡就必須是 true
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
