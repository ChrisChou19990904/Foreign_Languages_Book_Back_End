package org.example.repository;

import org.example.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 獲取某一會員的購物車所有明細
    List<CartItem> findByUserUserId(Long userId);

    // 檢查某一會員是否已將某一本書加入購物車
    Optional<CartItem> findByUserUserIdAndBookBookId(Long userId, Long bookId);

    // 檢查某個購物車明細是否屬於某個會員 (安全檢查用)
    boolean existsByCartItemIdAndUserUserId(Long cartItemId, Long userId);
}
