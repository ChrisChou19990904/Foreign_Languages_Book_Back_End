package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer reviewId;
    private String content;
    private Integer rating;
    private String username; // 🌟 關鍵：只給名稱，不給整個 User 物件
    private LocalDateTime createdAt;
}
