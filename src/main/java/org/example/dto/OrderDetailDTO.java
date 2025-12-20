// src/main/java/org/example/dto/OrderDetailDTO.java
package org.example.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class OrderDetailDTO {

    private Long orderId;
    private Long userId; // 🌟 新增這一行
    private String status;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private OffsetDateTime createdAt;
    // 🎯 收件資訊 (用於前端顯示)
    private String recipientName;
    private String shippingAddress;
    private String recipientPhone;
    // 🎯 訂單明細列表 (使用嵌套的 DTO)
    private List<OrderItemDTO> items;

    // 備註：您可能還需要 shippingFee
}
