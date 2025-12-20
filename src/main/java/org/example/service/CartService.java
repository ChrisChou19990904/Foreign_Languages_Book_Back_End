package org.example.service;

import org.example.dto.CartItemRequest;
import org.example.entity.Book;
import org.example.entity.CartItem;
import org.example.entity.User;
import org.example.repository.BookRepository;
import org.example.repository.CartItemRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * 獲取會員的購物車內容
     * @param userId 會員 ID
     * @return 購物車明細列表
     */
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserUserId(userId);
    }

    /**
     * 新增或更新購物車明細
     * @param userId 會員 ID
     * @param req 包含 bookId 和 quantity 的請求
     * @return 更新後的 CartItem
     */
    @Transactional
    public CartItem addOrUpdateCartItem(Long userId, CartItemRequest req) {
        if (req.getQuantity() == null || req.getQuantity() < 1) {
            throw new RuntimeException("購買數量必須大於 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new RuntimeException("書籍不存在"));

        // 檢查庫存
        if (req.getQuantity() > book.getStock()) {
            throw new RuntimeException("庫存不足！目前庫存為: " + book.getStock());
        }

        // 1. 檢查購物車中是否已存在該書
        Optional<CartItem> existingItem = cartItemRepository.findByUserUserIdAndBookBookId(userId, req.getBookId());

        if (existingItem.isPresent()) {
            // 2. 如果已存在，則更新數量 (覆蓋而非累加，通常前端會送出最終數量)
            CartItem item = existingItem.get();
            item.setQuantity(req.getQuantity());
            return cartItemRepository.save(item);
        } else {
            // 3. 如果不存在，則創建新的明細
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setBook(book);
            newItem.setQuantity(req.getQuantity());
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * 刪除購物車中的一個商品明細
     * @param userId 會員 ID
     * @param cartItemId 購物車明細 ID
     */
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        // 確保該 cartItemId 屬於該 userId，防止橫向越權
        if (!cartItemRepository.existsByCartItemIdAndUserUserId(cartItemId, userId)) {
            throw new RuntimeException("購物車明細不存在或不屬於當前會員");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
