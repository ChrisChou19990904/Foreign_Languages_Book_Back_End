package org.example.repository;

import org.example.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 會員查看自己的所有訂單 (前台功能)
    List<Order> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    // 🎯 2. 修復當前錯誤 (確保 OrderId 和 UserUserId 兩個條件都包含)
    boolean existsByOrderIdAndUserUserId(Long orderId, Long userId);
    // 🎯 核心修正 A: 會員前台詳情查詢
    // 必須使用 JOIN FETCH 強制載入 OrderItems (oi) 和 Book (b)
    // 同時加入用戶 ID 檢查，確保用戶只能查看自己的訂單
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.book b " +
            "WHERE o.orderId = :orderId AND o.user.userId = :userId")
    Optional<Order> findByIdAndUserIdWithDetails(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    // 🎯 核心修正 B: 管理員後台詳情查詢 (無需用戶 ID 檢查，但仍需 JOIN FETCH)
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.items oi " +
            "LEFT JOIN FETCH oi.book b " +
            "WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
}
