package org.example.dto;

import lombok.Data;

@Data
public class CartItemRequest {

    // 只需要傳入書本ID和數量
    private Long bookId;
    private Integer quantity;
}
