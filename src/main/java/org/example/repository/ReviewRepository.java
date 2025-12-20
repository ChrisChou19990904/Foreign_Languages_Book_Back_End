package org.example.repository;

import org.example.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 🎯 核心功能：根據書本 ID 找出所有評論，並按時間倒序排列（最新的在上面）
    List<Review> findByBook_BookIdOrderByCreatedAtDesc(Integer bookId);
}
