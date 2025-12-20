package org.example.dto;

import lombok.Data;

@Data
public class CheckoutResponseDTO {
    private Long orderId;
    private String message;
}
