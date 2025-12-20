package org.example.controller;

import org.example.dto.BookRequest;
import org.example.entity.Book;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 1. 書籍列表瀏覽 (Read All)
    // GET /api/admin/books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // 2. 新增書籍 (Create)
    // POST /api/admin/books
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookRequest request) {
        try {
            // 🎯 修正：直接調用新的 createBook(BookRequest request) 方法
            Book savedBook = bookService.createBook(request);
            return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // 現在錯誤可能是 ISBN 重複，或 Language Enum 不合法
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 修改書籍資訊 (Update)
    // PUT /api/admin/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookRequest request) { // 🎯 修正：接收 BookRequest DTO
        try {
            // 🎯 修正：調用新的 updateBook(Long id, BookRequest request) 方法
            final Book updatedBook = bookService.updateBook(id, request);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            // 錯誤可能是書籍 ID 未找到，或 Language Enum 不合法
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. 上架與下架控制 (Status Control)
    // PATCH /api/admin/books/{id}/status?onsale=true|false
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateBookStatus(@PathVariable Long id, @RequestParam boolean onsale) {
        try {
            final Book updatedBook = bookService.updateBookStatus(id, onsale);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
