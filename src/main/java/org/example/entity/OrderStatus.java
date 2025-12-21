package org.example.entity;

public enum OrderStatus {
    PENDING, // 待付款
    PAID,    // 已付款
    SHIPPED, // 已出貨
    DELIVERED, // 👈 新增這個

    DONE,    // 已完成
    CANCELLED // 已取消
}
