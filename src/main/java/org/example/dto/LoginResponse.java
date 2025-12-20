package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token; // 包含 JWT
    private String role;  // 包含 ADMIN 或 MEMBER，供前端判斷導向
}
