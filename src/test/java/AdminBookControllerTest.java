package org.example;

import org.example.controller.AdminBookController;
import org.example.entity.Book;
import org.example.service.BookService;
import org.example.service.JwtService;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AdminBookController.class)
@ContextConfiguration(classes = ForeignLanguagesBookApplication.class)
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    // --- HTML 報告邏輯開始 ---
    private static StringBuilder reportBuilder = new StringBuilder();

    @BeforeAll
    static void initReport() {
        reportBuilder.setLength(0);
        reportBuilder.append("<html><head><meta charset='UTF-8'><title>Controller 接口測試報告</title>")
                .append("<style>")
                .append("body{font-family:sans-serif;padding:20px;}")
                .append(".pass{color:green;font-weight:bold;}")
                .append(".fail{color:red;font-weight:bold;}")
                .append("table{border-collapse:collapse;width:100%;margin-top:20px;}")
                .append("th,td{border:1px solid #ccc;padding:10px;text-align:left;}")
                .append("th{background:#eef2f7;}")
                .append("</style>")
                .append("</head><body>")
                .append("<h1>後端 API 接口通訊測試報告</h1>")
                .append("<p>執行時間: ").append(LocalDateTime.now()).append("</p>")
                .append("<table><tr><th>測試項目</th><th>測試路徑</th><th>預期結果</th><th>耗時</th><th>狀態</th></tr>");
    }

    @AfterAll
    static void exportReport() {
        reportBuilder.append("</table></body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Controller_Test_Report.html"))) {
            writer.write(reportBuilder.toString());
            System.out.println("成功更新報告：Controller_Test_Report.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addReportRow(String name, String path, String expected, long startTime, boolean isPassed) {
        long duration = System.currentTimeMillis() - startTime;
        String statusLabel = isPassed ? "<span class='pass'>✅ PASSED</span>" : "<span class='fail'>❌ FAILED</span>";
        reportBuilder.append("<tr>")
                .append("<td>").append(name).append("</td>")
                .append("<td>").append(path).append("</td>")
                .append("<td>").append(expected).append("</td>")
                .append("<td>").append(duration).append(" ms</td>")
                .append("<td>").append(statusLabel).append("</td>")
                .append("</tr>");
    }
    // --- HTML 報告邏輯結束 ---

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("🧪 異常路徑：當 Service 報錯時，Controller 應回傳 400 Bad Request")
    void shouldReturnBadRequestWhenServiceFails() throws Exception {
        long start = System.currentTimeMillis();
        String testName = "Service 異常攔截";
        String apiPath = "POST /api/admin/books";
        try {
            when(bookService.createBook(any())).thenThrow(new RuntimeException("無效的語言分類: XYZ"));

            mockMvc.perform(post("/api/admin/books")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"title\":\"Test Book\", \"lang\":\"XYZ\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("無效的語言分類: XYZ"));

            addReportRow(testName, apiPath, "400 Bad Request", start, true);
        } catch (Throwable e) {
            addReportRow(testName, apiPath, "400 Bad Request", start, false);
            throw e;
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("🧪 正常路徑：成功切換書籍上下架狀態應回傳 200 OK")
    void shouldUpdateBookStatusSuccessfully() throws Exception {
        long start = System.currentTimeMillis();
        String testName = "上下架狀態切換";
        String apiPath = "PATCH /api/admin/books/1/status";
        try {
            when(bookService.updateBookStatus(eq(1L), any(Boolean.class))).thenReturn(new Book());

            mockMvc.perform(patch("/api/admin/books/1/status")
                            .with(csrf())
                            .param("onsale", "true"))
                    .andExpect(status().isOk());

            addReportRow(testName, apiPath, "200 OK", start, true);
        } catch (Throwable e) {
            addReportRow(testName, apiPath, "200 OK", start, false);
            throw e;
        }
    }
}
