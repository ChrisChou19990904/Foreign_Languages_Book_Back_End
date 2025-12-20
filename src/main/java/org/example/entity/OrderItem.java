package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    // 外鍵：連結 orders.order_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    // 🎯 核心修正：告訴 Jackson 在序列化 OrderItem 時忽略 order 屬性
    @JsonIgnore
    private Order order;

    // 外鍵：連結 books.book_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // 結帳當下的單價 (防止未來價格變動)

    private Integer quantity; // 購買數量

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal; // 小計 (price × quantity)
}
