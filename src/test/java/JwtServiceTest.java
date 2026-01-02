package org.example;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.service.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "ODhDNVg3dER1Z1E4V21rMmdWc3lQdkt0Y3JqZEdhSExoY1hXcG5yWThMOUU3PQ==";
    private final long jwtExpiration = 86400000;
    private UserDetails userDetails;

    // --- HTML 報告邏輯開始 ---
    private static StringBuilder reportBuilder = new StringBuilder();

    @BeforeAll
    static void initReport() {
        reportBuilder.setLength(0);
        reportBuilder.append("<html><head><meta charset='UTF-8'><title>安全性測試報告</title>")
                .append("<style>")
                .append("body{font-family:sans-serif;padding:20px;}")
                .append(".pass{color:green;font-weight:bold;}")
                .append(".fail{color:red;font-weight:bold;}")
                .append("table{border-collapse:collapse;width:100%;margin-top:20px;}")
                .append("th,td{border:1px solid #ccc;padding:10px;text-align:left;}")
                .append("th{background:#f0f7ff;}") // 安全測試使用淡藍色背景
                .append("</style>")
                .append("</head><body>")
                .append("<h1>系統安全維度：JWT 加密與身份驗證測試</h1>")
                .append("<p>執行時間: ").append(LocalDateTime.now()).append("</p>")
                .append("<table><tr><th>安全測試項目</th><th>驗證情境</th><th>預期防護結果</th><th>耗時</th><th>狀態</th></tr>");
    }

    @AfterAll
    static void exportReport() {
        reportBuilder.append("</table></body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Security_Test_Report.html"))) {
            writer.write(reportBuilder.toString());
            System.out.println("成功更新報告：Security_Test_Report.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addReportRow(String name, String scenario, String expected, long startTime, boolean isPassed) {
        long duration = System.currentTimeMillis() - startTime;
        String statusLabel = isPassed ? "<span class='pass'>✅ PASSED</span>" : "<span class='fail'>❌ FAILED</span>";
        reportBuilder.append("<tr>")
                .append("<td>").append(name).append("</td>")
                .append("<td>").append(scenario).append("</td>")
                .append("<td>").append(expected).append("</td>")
                .append("<td>").append(duration).append(" ms</td>")
                .append("<td>").append(statusLabel).append("</td>")
                .append("</tr>");
    }
    // --- HTML 報告邏輯結束 ---

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);

        userDetails = User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("產生 Token 並成功解析出用戶名")
    void testGenerateTokenAndExtractUsername() {
        long start = System.currentTimeMillis();
        try {
            String token = jwtService.generateToken((User) userDetails);
            String extractedUsername = jwtService.extractUsername(token);
            assertNotNull(token);
            assertEquals("test@example.com", extractedUsername);
            addReportRow("Token 簽發與解析", "產生 Token 並提取 Subject", "用戶名應精確匹配 test@example.com", start, true);
        } catch (Throwable e) {
            addReportRow("Token 簽發與解析", "解析失敗", "用戶名應精確匹配", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("Token 驗證：對於剛產生的 Token 應為有效")
    void testIsTokenValid_ValidToken() {
        long start = System.currentTimeMillis();
        try {
            String token = jwtService.generateToken((User) userDetails);
            assertTrue(jwtService.isTokenValid(token, userDetails));
            addReportRow("Token 有效性驗證", "驗證合法且未過期的 Token", "結果應為 True", start, true);
        } catch (Throwable e) {
            addReportRow("Token 有效性驗證", "合法 Token 被判斷為無效", "結果應為 True", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("Token 驗證：過期的 Token 應為無效")
    void testIsTokenValid_ExpiredToken() throws InterruptedException {
        long start = System.currentTimeMillis();
        try {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", "USER");
            String expiredToken = Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            Thread.sleep(10);
            assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
            addReportRow("過期安全性攔截", "模擬 Token 過期 10ms 後訪問", "應回傳 False (拒絕訪問)", start, true);
        } catch (Throwable e) {
            addReportRow("過期安全性攔截", "過期檢查邏輯失效", "應回傳 False", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("角色提取：應能正確解析出 Token 中的角色")
    void testExtractRole() {
        long start = System.currentTimeMillis();
        try {
            String token = jwtService.generateToken((User) userDetails);
            assertEquals("USER", jwtService.extractRole(token));
            addReportRow("權限 Claims 解析", "從 Claim 中提取角色數據", "應解析出角色: USER", start, true);
        } catch (Throwable e) {
            addReportRow("權限 Claims 解析", "角色提取失敗", "應解析出正確角色", start, false);
            throw e;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
