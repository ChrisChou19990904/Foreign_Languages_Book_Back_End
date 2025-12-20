package org.example.controller;

import org.example.dto.ProfileDto;
import org.example.dto.UserProfileResponse; // <-- 必須導入
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

/**
 * 用戶級別 API (需 JWT 認證才能訪問)
 * 路徑前綴：/api/user
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 讀取當前登入用戶的個人資料。
     * 路徑: GET /api/user/profile
     * 🚨 修正：返回 UserProfileResponse DTO
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        String userEmail = principal.getName();

        // 調用 Service 讀取資料，並返回結構化的 DTO
        UserProfileResponse response = userService.getProfileByEmail(userEmail);

        return ResponseEntity.ok(response);
    }

    /**
     * 更新當前登入用戶的個人資料。
     * 路徑: PUT /api/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(
            Principal principal,
            @RequestBody ProfileDto profileDto
    ) {
        String userEmail = principal.getName();

        try {
            // 調用 Service 執行更新 (使用 Email 作為查詢依據)
            userService.updateProfileByEmail(userEmail, profileDto);

            return ResponseEntity.ok("會員資料更新成功");
        } catch (RuntimeException e) {
            // 返回 400 Bad Request 和錯誤訊息
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
