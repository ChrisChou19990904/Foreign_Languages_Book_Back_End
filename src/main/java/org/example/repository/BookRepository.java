package org.example.repository;

import org.example.entity.Book;
import org.example.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 【前台功能】依語言篩選 (依賴 books.lang 欄位)
    List<Book> findByLangAndIsOnsaleTrue(Language lang);

    // 【前台功能】依關鍵字查詢 (Title/Author/ISBN) 且需為上架狀態
    @Query("SELECT b FROM Book b WHERE b.isOnsale = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "b.isbn LIKE CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    // 【後台功能】查看所有書籍
    List<Book> findAll();

    // 檢查 ISBN 是否重複
    boolean existsByIsbn(String isbn);
}
