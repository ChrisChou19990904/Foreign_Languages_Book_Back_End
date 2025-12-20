// src/main/java/org/example/dto/OrderStatusUpdateRequest.java
package org.example.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @Pattern(regexp = "PENDING|PAID|SHIPPED|DONE|CANCELLED", message = "新訂單狀態無效")
    private String newStatus;
}
