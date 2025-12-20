// src/main/java/org/example/exception/OrderNotFoundException.java

package org.example.exception;

// 讓它繼承 RuntimeException，這樣 Spring 就能在方法簽名中處理它
public class OrderNotFoundException extends RuntimeException {

    // 只需要一個接受錯誤訊息的建構函式
    public OrderNotFoundException(String message) {
        super(message);
    }

    // 您也可以添加其他建構函式，例如接受訂單 ID
    public OrderNotFoundException(Long orderId) {
        super("找不到訂單 ID: " + orderId);
    }
}
