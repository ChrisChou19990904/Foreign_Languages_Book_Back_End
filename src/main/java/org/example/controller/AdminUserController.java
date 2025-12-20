package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserResponse; // 剛才建立的 DTO
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users") // 統一管理員路徑規範
@RequiredArgsConstructor // 自動幫你注入 userService
public class AdminUserController {

    private final UserService userService;

    /**
     * 1. 獲取所有會員列表 (後台管理用)
     * GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        // 這裡可以參考你 AdminOrderController 的 Debug 模式，
        // 確認目前的身份是否有 ROLE_ADMIN
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * 2. 切換會員狀態 (啟用/停權)
     * PATCH /api/admin/users/{userId}/toggle-active
     */
    @PatchMapping("/{userId}/toggle-active")
    public ResponseEntity<?> toggleUserActive(@PathVariable Integer userId) {
        try {
            userService.toggleUserActive(userId);
            return ResponseEntity.ok(Map.of("message", "用戶狀態已成功更新"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 3. 修改用戶角色 (例如將 USER 改為 ADMIN)
     * PATCH /api/admin/users/{userId}/role
     * Body: {"role": "ADMIN"}
     */
    @PatchMapping("/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Integer userId, @RequestBody Map<String, String> payload) {
        try {
            String newRole = payload.get("role");
            userService.updateUserRole(userId, newRole);
            return ResponseEntity.ok(Map.of("message", "用戶角色已更新為 " + newRole));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
