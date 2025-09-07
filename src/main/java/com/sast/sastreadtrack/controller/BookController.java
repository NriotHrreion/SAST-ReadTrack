package com.sast.sastreadtrack.controller;

import com.sast.sastreadtrack.entity.Book;
import com.sast.sastreadtrack.service.BookService;
import com.sast.sastreadtrack.service.UserService;
import com.sast.sastreadtrack.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书籍控制器
 * 处理书籍的CRUD、进度更新、查询等请求
 */
@RestController
@RequestMapping("/api/book")
public class BookController {
    public static final String TOKEN_HEADER = "X-Credential-Token";

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;

    /**
     * 添加新书籍
     * @param userId 用户ID
     * @param book 书籍信息（包含书名、作者、总页数）
     * @param token 会话token
     */
    @PostMapping("/{userId}/add")
    public ResponseEntity<Map<String, Object>> addBook(
            @PathVariable Long userId,
            @RequestBody Book book,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }
        if(!book.getUserId().equals(userId)) {
            return ResponseMessage.of(HttpStatus.BAD_REQUEST, "用户id与书籍id不一致");
        }
        if(!bookService.addBook(book)) {
            return ResponseMessage.of(HttpStatus.BAD_REQUEST, "添加书籍失败");
        }
        return ResponseMessage.ok();
    }

    /**
     * 更新阅读进度
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param currentPage 已读页数
     * @param token 会话token
     */
    @PutMapping("/{userId}/{bookId}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestParam Integer currentPage,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }
        if(bookService.getUserBook(userId, bookId) == null) {
            return ResponseMessage.of(HttpStatus.NOT_FOUND, "找不到指定书籍");
        }
        if(!bookService.updateReadProgress(bookId, currentPage, userId)) {
            return ResponseMessage.of(HttpStatus.BAD_REQUEST, "无法更新阅读进度");
        }
        return ResponseMessage.ok();
    }

    /**
     * 更新阅读状态
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param status 阅读状态（未读/阅读中/已读）
     * @param token 会话token
     */
    @PutMapping("/{userId}/{bookId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestParam String status,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }
        if(bookService.getUserBook(userId, bookId) == null) {
            return ResponseMessage.of(HttpStatus.NOT_FOUND, "找不到指定书籍");
        }
        if(!bookService.updateStatus(bookId, status, userId)) {
            return ResponseMessage.of(HttpStatus.BAD_REQUEST, "无法更新阅读状态");
        }
        return ResponseMessage.ok();
    }

    /**
     * 删除书籍（仅能删除自己的书籍）
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param token 会话token
     */
    @DeleteMapping("/{userId}/{bookId}")
    public ResponseEntity<Map<String, Object>> deleteBook(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }
        if(bookService.getUserBook(userId, bookId) == null) {
            return ResponseMessage.of(HttpStatus.NOT_FOUND, "找不到指定书籍");
        }
        if(!bookService.deleteBook(bookId, userId)) {
            return ResponseMessage.of(HttpStatus.BAD_REQUEST, "无法删除书籍");
        }
        return ResponseMessage.ok();
    }

    /**
     * 查询用户的书籍列表
     * @param userId 用户ID
     * @param status 阅读状态（可选）
     * @param token 会话token
     */
    @GetMapping("/{userId}/list")
    public ResponseEntity<Map<String, Object>> getBooks(
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> res = new HashMap<>();
        if(status == null) {
            res.put("list", bookService.getUserBooks(userId));
            return ResponseMessage.ok(res);
        }
        res.put("list", bookService.getBooksByStatus(userId, status));
        return ResponseMessage.ok(res);
    }

    /**
     * 模糊查询书籍
     * @param userId 用户ID
     * @param keyword 关键词
     * @param token 会话token
     */
    @GetMapping("/{userId}/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @PathVariable Long userId,
            @RequestParam String keyword,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("list", bookService.searchBooks(keyword, userId));
        return ResponseMessage.ok(res);
    }

    /**
     * 获取阅读统计信息
     * @param userId 用户ID
     * @param token 会话token
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getReadingStats(
            @PathVariable Long userId,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }

        Book stat = bookService.getReadingStats(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("current_page", stat.getCurrentPage());
        res.put("total_pages", stat.getTotalPages());
        res.put("progress", stat.getCurrentPage() / stat.getTotalPages());
        return ResponseMessage.ok(res);
    }

    /**
     * 获取用户书籍数量
     * @param userId 用户ID
     * @param token 会话token
     */
    @GetMapping("/{userId}/amount")
    public ResponseEntity<Map<String, Object>> getBookAmount(
            @PathVariable Long userId,
            @RequestHeader(TOKEN_HEADER) String token) {
        if(!userService.authToken(userId, token)) {
            return ResponseMessage.of(HttpStatus.UNAUTHORIZED);
        }

        List<Book> list = bookService.getUserBooks(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("amount", list.size());
        return ResponseMessage.ok(res);
    }
}
