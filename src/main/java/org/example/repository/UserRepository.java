package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 登入用：根據 email 查找
    Optional<User> findByEmail(String email);

    // 註冊檢查用：確認 email 是否已存在
    boolean existsByEmail(String email);
}
