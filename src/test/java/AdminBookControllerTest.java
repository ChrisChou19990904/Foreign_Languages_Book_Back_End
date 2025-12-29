package org.example;

import org.example.controller.AdminBookController;
import org.example.entity.Book;
import org.example.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// 靜態導入：包含了請求構建與結果驗證
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookController.class)
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("🧪 異常路徑：當 Service 報錯時，Controller 應回傳 400 Bad Request")
    void shouldReturnBadRequestWhenServiceFails() throws Exception {
        // 模擬 Service 拋出 RuntimeException
        when(bookService.createBook(any())).thenThrow(new RuntimeException("無效的語言分類: XYZ"));

        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Book\", \"lang\":\"XYZ\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("無效的語言分類: XYZ"));
    }

    @Test
    @DisplayName("🧪 正常路徑：成功切換書籍上下架狀態應回傳 200 OK")
    void shouldUpdateBookStatusSuccessfully() throws Exception {
        // 模擬：當更新 ID 為 1 的書籍狀態時，Service 回傳一個成功的 Book 物件
        when(bookService.updateBookStatus(eq(1L), any(Boolean.class)))
                .thenReturn(new Book());

        // 執行 PATCH 請求，驗證管理端的狀態控制功能
        mockMvc.perform(patch("/api/admin/books/1/status")
                        .param("onsale", "true"))
                .andExpect(status().isOk());
    }
}
