package org.example.dto;

import lombok.Data;

@Data // Lombok 簡化 Getter/Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;

    // 不需 role, is_active, created_at，由後端強制設定
}