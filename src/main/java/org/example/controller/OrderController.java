package org.example.controller;

import org.example.dto.CheckoutRequest;
import org.example.dto.CheckoutResponseDTO;
import org.example.dto.OrderDetailDTO;
import org.example.dto.OrderListDTO;
import org.example.entity.Order;
import org.example.entity.OrderStatus;
import org.example.entity.PaymentMethod;
import org.example.entity.User;
import org.example.exception.OrderNotFoundException;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.security.Principal; // 引入 Principal
@RestController
@RequestMapping("/api/user/orders") // 僅限 MEMBER 或 ADMIN 訪問
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    public OrderController(OrderService orderService, UserRepository userRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    // 輔助方法：獲取當前登入者 ID
    private Long getCurrentUserId() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("無法獲取登入會員資訊"));
        return user.getUserId();
    }

    // 🎯 附帶檢查：修改 getMyOrderDetail 也使用 Principal，更標準

    // 1. 結帳 (Create Order)
    // POST /api/user/orders/checkout
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout(@Valid @RequestBody CheckoutRequest req) {
        Long userId = getCurrentUserId();

        // 🎯 2. 確認變數類型為 CheckoutResponseDTO
        CheckoutResponseDTO responseDTO = orderService.checkout(userId, req);

        // 🎯 3. 確認返回的實例是 responseDTO (CheckoutResponseDTO 類型)
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 2. 查詢會員所有訂單
    // GET /api/user/orders
    @GetMapping
    public ResponseEntity<List<OrderListDTO>> getMyOrders() {
        Long userId = getCurrentUserId();
        List<OrderListDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // 3. 查詢單筆訂單詳情
    // GET /api/user/orders/{orderId}
    // src/main/java/org/example/controller/OrderController.java (修正後)

    // 3. 查詢單筆訂單詳情
// GET /api/user/orders/{orderId}
    @GetMapping("/{orderId}")
// 🎯 修正 1: 返回類型必須是 OrderDetailDTO
    public ResponseEntity<OrderDetailDTO> getMyOrderDetail(@PathVariable Long orderId) {
        // 🎯 修正 2: 只需要這一個變量來獲取當前用戶 ID
        Long userId = getCurrentUserId();

        // 🎯 修正 3: 正確調用 Service 方法，傳入正確的 ID
        OrderDetailDTO detailDTO = orderService.getOrderDetailByIdAndUserId(orderId, userId);

        // 🎯 修正 4: 返回 DTO
        return ResponseEntity.ok(detailDTO);
    }

    // 🎯 處理 OrderNotFoundException
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 返回 404
    public Map<String, String> handleOrderNotFound(OrderNotFoundException ex) {
        return Map.of("message", ex.getMessage());
    }

    @PatchMapping("/{orderId}/complete-payment")
    @Transactional // 🌟 務必確保有這個註解，否則 save 有可能不生效
    public ResponseEntity<?> completePayment(@PathVariable Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            // 1. 確保狀態變更
            order.setStatus(OrderStatus.PAID);

            // 🌟 2. 這是關鍵！強制把付款方式更新為 CREDIT_CARD
            // 這樣明細頁抓出來的資料就會是正確的
            order.setPaymentMethod(PaymentMethod.valueOf("CREDIT_CARD"));

            orderRepository.save(order);

            // 加個 Log 給自己看，確認有執行到
            System.out.println("訂單 " + orderId + " 已更新：PAID 且 CREDIT_CARD");

            return ResponseEntity.ok("付款成功");
        }).orElse(ResponseEntity.notFound().build());
    }
}
