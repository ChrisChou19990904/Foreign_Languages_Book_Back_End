package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ReviewResponse;
import org.example.entity.Review;
import org.example.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/public/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Integer bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    @PostMapping("/user/reviews")
    public ResponseEntity<?> postReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload
    ) {
        // 🌟 在這裡加一行，去後端控制台 (IntelliJ Console) 看
        System.out.println("收到評論請求: " + payload);
        reviewService.addReview(
                userDetails.getUsername(),
                (Integer) payload.get("bookId"),
                (Integer) payload.get("rating"),
                (String) payload.get("content")
        );
        return ResponseEntity.ok("✅ 評論發表成功！");
    }
}
