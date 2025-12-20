package org.example.service;

import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 處理新用戶註冊
     * @param request 註冊請求 DTO
     * @return 包含新生成 JWT 的響應
     */
    public AuthenticationResponse register(RegisterRequest request) {

        // 檢查 Email 是否已存在
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        // 建立新的 User 實體
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                // 密碼加密
                .password(passwordEncoder.encode(request.getPassword()))
                // 預設給予 USER 權限
                .role(Role.USER)
                .build();

        // 註冊時，可以設置第一個註冊的人為 ADMIN
        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN);
        }

        userRepository.save(user);

        // 生成 JWT Token
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }

    /**
     * 處理用戶登入
     * @param request 登入請求 DTO
     * @return 包含 JWT 的響應
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // 1. 執行身份驗證：如果憑證無效，將拋出 AuthenticationException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. 驗證成功後，從數據庫載入用戶詳細信息
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. 生成 JWT Token
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }
}
