package org.example.config;

import org.example.entity.User;
import org.example.entity.Role;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    // 專案啟動時，檢查是否已存在 admin@test.com 帳號，若無則建立
    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            final String adminEmail = "admin@test.com";

            if (!repo.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode("admin123")); // 請在實際環境中修改或使用安全的密碼
                admin.setRole(Role.ADMIN);
                // created_at 和 is_active 由 Entity 的 @PrePersist 自動處理
                repo.save(admin);
                System.out.println("✅ 管理員帳號 admin@test.com (密碼: admin123) 已自動建立。");
            }
        };
    }
}
