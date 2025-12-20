package org.example.controller;

import org.example.dto.OrderDetailDTO;
import org.example.dto.OrderListDTO;
import org.example.entity.Order;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders") // 僅限 ADMIN 訪問
public class AdminOrderController {

    private final OrderService orderService;
    private UserRepository userRepository;
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. 訂單清單瀏覽 (Read All)
    // GET /api/admin/orders
    @GetMapping
    public ResponseEntity<List<OrderListDTO>> getAllOrders() {
        // =========================================================
        // 🎯 偵錯程式碼：請將此段加入並重新啟動伺服器
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 這行會將結果打印在伺服器控制台/日誌中
        System.out.println("=================================================");
        System.out.println("!!! DEBUG authorities for admin@test.com: " + authorities);
        System.out.println("=================================================");
        // =========================================================

        List<OrderListDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // 2. 訂單狀態更新
    // PATCH /api/admin/orders/{orderId}/status
    // 請求體示例: {"status": "shipped"}
    // AdminOrderController.java (修正後)

    @PatchMapping("/{orderId}/status")
// 🎯 變更返回類型為 DTO
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                                            @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("缺少新的訂單狀態參數");
            }

            // 🎯 讓 Service 返回 DTO，而非 Order 實體
            OrderDetailDTO updatedOrderDto = orderService.updateOrderStatusAndGetDetail(orderId, newStatus);

            return ResponseEntity.ok(updatedOrderDto); // 返回 DTO
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // src/main/java/org/example/controller/OrderController.java (訂單詳情方法)

    // 3. 管理員看訂單詳情 (不需要檢查 userId)
    // GET /api/admin/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDTO> getAdminOrderDetail(@PathVariable Long orderId) {
        // 🎯 這裡要呼叫一個「只靠 OrderId」就能抓資料的方法
        // 不要使用 getOrderDetailByIdAndUserId，因為管理員不是購買者
        OrderDetailDTO detailDTO = orderService.getOrderDetailByOrderIdOnly(orderId);

        return ResponseEntity.ok(detailDTO);
    }
}
