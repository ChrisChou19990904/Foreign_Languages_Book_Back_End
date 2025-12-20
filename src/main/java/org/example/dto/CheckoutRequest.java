package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CheckoutRequest {

    // 訂單主表資訊
    @NotBlank(message = "必須指定付款方式")
    @Pattern(regexp = "CREDIT_CARD|CASH_ON_DELIVERY", message = "付款方式無效")
    private String paymentMethod;

    // 模擬運送與收件資訊 (文件未詳細說明，但結帳流程必須有)
    @NotBlank(message = "收件人姓名不能為空")
    private String recipientName;

    @NotBlank(message = "收件地址不能為空")
    private String shippingAddress;

    private String recipientPhone;
}
