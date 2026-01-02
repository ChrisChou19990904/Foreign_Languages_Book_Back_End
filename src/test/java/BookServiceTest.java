package org.example;

import org.example.dto.BookRequest;
import org.example.entity.Book;
import org.example.repository.BookRepository;
import org.example.service.BookService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private static StringBuilder reportBuilder = new StringBuilder();

    @BeforeAll
    static void initReport() {
        reportBuilder.setLength(0); // 清空舊內容
        reportBuilder.append("<html><head><meta charset='UTF-8'><title>進階邊界測試報告</title>")
                .append("<style>")
                .append("body{font-family:sans-serif;padding:20px;}")
                .append(".pass{color:green;font-weight:bold;}")
                .append(".fail{color:red;font-weight:bold;}")
                .append("table{border-collapse:collapse;width:100%;margin-top:20px;}")
                .append("th,td{border:1px solid #ccc;padding:10px;text-align:left;}")
                .append("th{background:#f4f4f4;}")
                .append("</style>")
                .append("</head><body>")
                .append("<h1>全端自動化測試：邊界與邏輯專項報告</h1>")
                .append("<p>執行時間: ").append(LocalDateTime.now()).append("</p>")
                .append("<table><tr><th>測試項目</th><th>測試數據</th><th>預期結果</th><th>耗時</th><th>狀態</th></tr>");
    }

    @AfterAll
    static void exportReport() {
        reportBuilder.append("</table></body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Boundary_Test_Report.html"))) {
            writer.write(reportBuilder.toString());
            System.out.println("成功更新報告：Boundary_Test_Report.html");
        } catch (IOException e) {
            System.err.println("檔案被佔用或寫入錯誤: " + e.getMessage());
        }
    }

    // 更新後的輔助方法：支援顯示 PASSED 或 FAILED
    private void addReportRow(String name, String data, String expected, long startTime, boolean isPassed) {
        long duration = System.currentTimeMillis() - startTime;
        String statusLabel = isPassed ? "<span class='pass'>✅ PASSED</span>" : "<span class='fail'>❌ FAILED</span>";

        reportBuilder.append("<tr>")
                .append("<td>").append(name).append("</td>")
                .append("<td>").append(data).append("</td>")
                .append("<td>").append(expected).append("</td>")
                .append("<td>").append(duration).append(" ms</td>")
                .append("<td>").append(statusLabel).append("</td>")
                .append("</tr>");
    }

    @Test
    @DisplayName("測試正常新增書籍 (Happy Path)")
    void testCreateBookSuccess() {
        long start = System.currentTimeMillis();
        String testName = "正常新增書籍";
        String testData = "Price=500, Title=Java...";
        try {
            BookRequest request = new BookRequest();
            request.setTitle("Java 程式設計");
            request.setIsbn("1234567890123");
            request.setPrice(BigDecimal.valueOf(500));
            request.setLang("ENGLISH");

            when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
            when(bookRepository.save(any(Book.class))).thenReturn(new Book());

            assertDoesNotThrow(() -> bookService.createBook(request));
            addReportRow(testName, testData, "成功寫入資料庫", start, true);
        } catch (Throwable e) {
            addReportRow(testName, testData, "預期成功但發生錯誤", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("測試價格邊界：負數攔截")
    void testCreateBookWithNegativePrice() {
        long start = System.currentTimeMillis();
        String testName = "價格邊界攔截";
        String testData = "Price = -1";
        try {
            BookRequest request = new BookRequest();
            request.setPrice(BigDecimal.valueOf(-1));
            request.setTitle("測試書籍");
            request.setLang("ENGLISH");

            // 如果這裡沒有噴出異常，AssertionFailedError 會被 catch 住並記錄為 FAILED
            assertThrows(RuntimeException.class, () -> bookService.createBook(request));
            addReportRow(testName, testData, "拋出異常並拒絕存檔", start, true);
        } catch (Throwable e) {
            addReportRow(testName, testData, "未能成功攔截負數價格", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("測試標題邊界：空白標題攔截")
    void testCreateBookWithEmptyTitle() {
        long start = System.currentTimeMillis();
        String testName = "標題邊界攔截";
        String testData = "Title = '' (Empty)";
        try {
            BookRequest request = new BookRequest();
            request.setTitle("");
            request.setPrice(BigDecimal.valueOf(100));
            request.setLang("ENGLISH");

            assertThrows(RuntimeException.class, () -> bookService.createBook(request));
            addReportRow(testName, testData, "禁止創建無標題書籍", start, true);
        } catch (Throwable e) {
            addReportRow(testName, testData, "未能成功攔截空白標題", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("測試 ISBN 重複攔截")
    void testCreateBookWithDuplicateIsbn() {
        long start = System.currentTimeMillis();
        String testName = "ISBN 重複攔截";
        String testData = "Isbn = 123456";
        try {
            BookRequest request = new BookRequest();
            request.setIsbn("123456");
            request.setTitle("Some Title");
            request.setLang("ENGLISH");
            when(bookRepository.existsByIsbn("123456")).thenReturn(true);

            assertThrows(RuntimeException.class, () -> bookService.createBook(request));
            addReportRow(testName, testData, "拋出重複異常", start, true);
        } catch (Throwable e) {
            addReportRow(testName, testData, "未能檢測出 ISBN 重複", start, false);
            throw e;
        }
    }

    @Test
    @DisplayName("測試有效與無效的語言解析")
    void testParseLanguage() {
        long start = System.currentTimeMillis();
        String testName = "語言解析驗證";
        String testData = "Input: french / klingon";
        try {
            bookService.getOnsaleBooksByLang("french"); // 正常
            assertThrows(RuntimeException.class, () -> bookService.getOnsaleBooksByLang("klingon")); // 異常

            addReportRow(testName, testData, "成功解析合法語言並攔截非法語言", start, true);
        } catch (Throwable e) {
            addReportRow(testName, testData, "語言解析邏輯錯誤", start, false);
            throw e;
        }
    }
}
