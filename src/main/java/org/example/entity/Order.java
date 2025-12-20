package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 移除 "items" 可能導致前端無法看到訂單明細
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;// 訂單狀態：pending / paid / shipped / done
    // 🎯 記得要加上收件人資訊，否則前端永遠讀不到
    private String recipientName;
    private String shippingAddress;

    @Column(name = "recipient_phone") // 🌟 確保名稱正確
    private String recipientPhone;
    // 外鍵：連結 users.user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice; // 訂單總金額

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;// 付款方式：credit_card 或 cod


    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    // 與訂單明細的一對多關係
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    public Long getUserId() {
        // 確保 User 關聯非空 (即使是 Lazy Loading 代理)
        if (this.user != null) {
            return this.user.getUserId(); // 假設 User 實體有 getUserId()
        }
        return null;
    }
    // 在持久化之前自動設置 created_at
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
