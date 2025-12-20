package org.example.repository;

import org.example.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 訂單明細通常只在 Order 實體被查詢時被級聯載入，單獨操作較少
}
