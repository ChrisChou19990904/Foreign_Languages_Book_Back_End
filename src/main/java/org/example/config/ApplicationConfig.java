package org.example.config;

import org.example.repository.UserRepository; // 假設您有 UserRepository
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 應用程式安全基礎配置，定義關鍵的 Bean
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository; // 注入您的用戶數據庫操作介面

    /**
     * 從數據庫載入用戶詳細信息
     * @return UserDetailsService 實例
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                // 🎯 關鍵修復：必須處理 Optional，如果找不到用戶，則拋出 UsernameNotFoundException
                .orElseThrow(() -> new UsernameNotFoundException("用戶找不到: " + email));
    }

    /**
     * 認證提供者 (AuthenticationProvider)
     * 告訴 Spring Security 如何獲取用戶詳情和如何驗證密碼
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // 使用我們定義的密碼編碼器
        return authProvider;
    }

    /**
     * 認證管理器 (AuthenticationManager)
     * 用於在登入時執行認證
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 密碼編碼器 (PasswordEncoder)
     * 使用 BCrypt 算法加密密碼
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}