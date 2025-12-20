package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookRequest {

    // 必填欄位 (與您 Controller 接收的 JSON 欄位一致)
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Integer stock;

    // 🎯 關鍵：Language Enum 分類
    // 接收前端傳來的語言字串，如 "JAPANESE" 或 "ENGLISH"
    private String lang;

    // 可選欄位
    private String description;
    private String imageUrl;
    private Boolean isOnsale;
    private LocalDate publishedDate;
}
