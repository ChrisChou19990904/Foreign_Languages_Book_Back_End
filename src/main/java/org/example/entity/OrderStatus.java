package org.example.entity;

public enum OrderStatus {
    PENDING, // 待付款
    AWAITING_PAYMENT, // 👈 關鍵新增：待支付確認（信用卡跳轉中）
    PROCESSING,       // 👈 建議新增：金流處理中（對接銀行驗證中）
    PAID,    // 已付款
    SHIPPED, // 已出貨
    DELIVERED, // 👈 新增這個

    DONE,    // 已完成
    CANCELLED // 已取消
}
