package org.example;

import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url" +
                "=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;NON_KEYWORDS=USER",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class AdminInitializerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    // --- HTML 報告邏輯開始 ---
    private static StringBuilder reportBuilder = new StringBuilder();

    @BeforeAll
    static void initReport() {
        reportBuilder.setLength(0);
        reportBuilder.append("<html><head><meta charset='UTF-8'><title>整合測試報告</title>")
                .append("<style>")
                .append("body{font-family:sans-serif;padding:20px;}")
                .append(".pass{color:green;font-weight:bold;}")
                .append(".fail{color:red;font-weight:bold;}")
                .append("table{border-collapse:collapse;width:100%;margin-top:20px;}")
                .append("th,td{border:1px solid #ccc;padding:10px;text-align:left;}")
                .append("th{background:#fdf2f2;}") // 整合測試用稍微不同的顏色區分
                .append("</style>")
                .append("</head><body>")
                .append("<h1>系統整合測試：資料庫初始化驗證報告</h1>")
                .append("<p>執行時間: ").append(LocalDateTime.now()).append("</p>")
                .append("<p>測試環境: H2 Memory Database (Profile: test)</p>")
                .append("<table><tr><th>測試項目</th><th>驗證邏輯</th><th>預期結果</th><th>耗時</th><th>狀態</th></tr>");
    }

    @AfterAll
    static void exportReport() {
        reportBuilder.append("</table></body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Integration_Test_Report.html"))) {
            writer.write(reportBuilder.toString());
            System.out.println("成功更新報告：Integration_Test_Report.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addReportRow(String name, String logic, String expected, long startTime, boolean isPassed) {
        long duration = System.currentTimeMillis() - startTime;
        String statusLabel = isPassed ? "<span class='pass'>✅ PASSED</span>" : "<span class='fail'>❌ FAILED</span>";
        reportBuilder.append("<tr>")
                .append("<td>").append(name).append("</td>")
                .append("<td>").append(logic).append("</td>")
                .append("<td>").append(expected).append("</td>")
                .append("<td>").append(duration).append(" ms</td>")
                .append("<td>").append(statusLabel).append("</td>")
                .append("</tr>");
    }
    // --- HTML 報告邏輯結束 ---

    @Test
    @DisplayName("整合測試：應用程式啟動後，資料庫應包含預設管理員")
    void testAdminCreatedOnStartup() {
        long start = System.currentTimeMillis();
        String testName = "預設管理員初始化驗證";
        String logic = "檢查啟動後 UserRepository 是否包含 admin@test.com";

        try {
            // Act
            Optional<User> adminUser = userRepository.findByEmail("admin@test.com");

            // Assert
            assertTrue(adminUser.isPresent(), "管理員帳號應該在啟動時自動建立");
            assertEquals("admin", adminUser.get().getRealName());
            assertEquals(Role.ADMIN, adminUser.get().getRole());

            addReportRow(testName, logic, "找到 Admin 帳號且權限正確", start, true);
            System.out.println("✅ 整合測試通過：已確認資料庫中存在 admin@test.com");
        } catch (Throwable e) {
            addReportRow(testName, logic, "找到 Admin 帳號且權限正確", start, false);
            throw e;
        }
    }
}
