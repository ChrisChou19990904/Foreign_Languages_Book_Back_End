// src/main/java/org/example/dto/OrderSummaryDTO.java
package org.example.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class OrderSummaryDTO {

    private Long orderId;
    private Long userId;            // 列表頁通常需要用戶ID
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
    private OffsetDateTime createdAt;

    // 🎯 包含收件人信息，供管理員列表使用
    private String recipientName;
}