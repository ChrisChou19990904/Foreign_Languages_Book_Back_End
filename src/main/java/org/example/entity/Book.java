package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(length = 100)
    private String author;

    @Column(unique = true, length = 20)
    private String isbn; // 國際標準書號

    // NUMERIC(10, 2) 對應 Java 的 BigDecimal
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private Integer stock; // 庫存量

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "lang", length = 50, nullable = false) // 設定為不可為空
    private Language lang;

    // 學習語言分類 (english, japanese, french)

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl; // 封面圖片網址

    @Column(name = "is_onsale")
    private Boolean isOnsale; // 上架(TRUE) / 下架(FALSE)

    @Column(name = "published_date")
    private LocalDate publishedDate;
}
