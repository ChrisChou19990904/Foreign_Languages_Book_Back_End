// src/main/java/org/example/dto/OrderItemDTO.java
package org.example.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {

    private Long orderItemId;
    private Integer quantity;

    // 🎯 修正：使用 price (與您的前端模板最接近)
    private BigDecimal price;
    private BigDecimal subtotal;

    // 🎯 書籍信息 (用於前端顯示名稱和跳轉 ID)
    private Long bookId;
    private String bookTitle;
}
