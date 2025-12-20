package org.example.controller;

import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.dto.RegisterRequest;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// 🚨 修正：將類別級別的映射從 "/api" 更改為 "/api/auth"
@RequestMapping("/api/auth")
// 為了確保 CORS 預檢請求通過，加上 @CrossOrigin 註解
// 即使我們在 SecurityConfig 中有 FilterRegistrationBean，這個註解作為輔助也很重要
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173", "*"})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 1. 註冊 API：最終路徑為 /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            userService.registerUser(req);
            return ResponseEntity.ok("註冊成功");
        } catch (RuntimeException e) {
            // 處理業務邏輯錯誤（如用戶名重複等）
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 登入 API：最終路徑為 /api/auth/login
    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            String[] result = userService.loginUser(req);
            String token = result[0];
            String role = result[1];

            // 回傳 Token 和 角色
            return ResponseEntity.ok(new LoginResponse(token, role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
