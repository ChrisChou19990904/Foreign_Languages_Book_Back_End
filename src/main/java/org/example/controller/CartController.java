package org.example.controller;

import org.example.dto.CartItemRequest;
import org.example.entity.CartItem;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user/cart") // 僅限 MEMBER 或 ADMIN 訪問
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository; // 用於獲取當前登入者 ID

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    // 輔助方法：從 Security Context 獲取當前登入者的 User ID
    private Long getCurrentUserId() {
        // 獲取 JWT 中的 email (sub/name)
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 根據 email 查找 user 實體以獲取 ID
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("無法獲取登入會員資訊"));

        return user.getUserId();
    }

    // GET /api/user/cart
    // 獲取會員購物車明細 (Read)
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        Long userId = getCurrentUserId();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // POST /api/user/cart
    // 新增或更新購物車商品 (Add/Update)
    @PostMapping
    public ResponseEntity<?> addOrUpdateItem(@RequestBody CartItemRequest req) {
        System.out.println("DEBUG: Request received for adding Book ID: " + req.getBookId());
        try {
            Long userId = getCurrentUserId();
            CartItem updatedItem = cartService.addOrUpdateCartItem(userId, req);
            return new ResponseEntity<>(updatedItem, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/user/cart/{cartItemId}
    // 刪除購物車中的一個明細 (Delete)
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long cartItemId) {
        try {
            Long userId = getCurrentUserId();
            cartService.deleteCartItem(userId, cartItemId);
            return ResponseEntity.ok("購物車明細已移除");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
