package org.example.dto;// src/main/java/org/example/dto/OrderListDTO.java

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class OrderListDTO {
    private Long orderId;
    private String status;
    private BigDecimal totalPrice;
    private OffsetDateTime createdAt;
    // ... 根據列表頁需求添加其他基本屬性
// 🌟 補上這個欄位
    private String paymentMethod;
    // 構造函數, Getters, Setters...
}
