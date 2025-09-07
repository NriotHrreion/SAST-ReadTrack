package com.sast.sastreadtrack.service.impl;

import com.sast.sastreadtrack.entity.Book;
import com.sast.sastreadtrack.mapper.BookMapper;
import com.sast.sastreadtrack.service.BookService;
import com.sast.sastreadtrack.utils.ReadingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 书籍服务层实现类
 */
@Service
public class BookServiceImpl implements BookService {

    @Resource
    private BookMapper bookMapper;

    @Override
    @Transactional
    public boolean addBook(Book book) {
        List<Book> bookList = bookMapper.selectByUserId(book.getUserId());
        for(Book item : bookList) {
            if(item.getTitle().equals(book.getTitle()) && item.getAuthor().equals(book.getAuthor())) {
                return false;
            }
        }
        if(book.getTotalPages() <= 0 || book.getCurrentPage() != 0) return false;

        bookMapper.insert(book);
        return true;
    }

    @Override
    @Transactional
    public boolean updateReadProgress(Long bookId, Integer currentPages, Long userId) {
        Book book = bookMapper.selectByUserIdAndBookId(userId, bookId);
        if(book == null) return false;

        if(currentPages < book.getTotalPages()) {
            book.setCurrentPage(currentPages);
            book.setStatus(ReadingStatus.READING);
            bookMapper.updateReadProgress(book);
            return true;
        }
        if(currentPages.equals(book.getTotalPages())) {
            book.setCurrentPage(currentPages);
            book.setStatus(ReadingStatus.FINISHED);
            bookMapper.updateReadProgress(book);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long bookId, String status, Long userId) {
        Book book = bookMapper.selectByUserIdAndBookId(userId, bookId);
        if(book == null) return false;
        if(!ReadingStatus.isValid(status)) return false;

        book.setStatus(status);
        bookMapper.updateStatus(book);
        if(status.equals(ReadingStatus.FINISHED)) {
            book.setCurrentPage(book.getTotalPages());
            bookMapper.updateReadProgress(book);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteBook(Long bookId, Long userId) {
        Book book = bookMapper.selectByUserIdAndBookId(userId, bookId);
        if(book == null) return false;

        bookMapper.deleteById(bookId, userId);
        return true;
    }

    @Override
    public List<Book> getUserBooks(Long userId) {
        return bookMapper.selectByUserId(userId);
    }

    @Override
    public Book getUserBook(Long userId, Long bookId) {
        return bookMapper.selectByUserIdAndBookId(userId, bookId);
    }

    @Override
    public List<Book> getBooksByStatus(Long userId, String status) {
        if(!ReadingStatus.isValid(status)) return new ArrayList<>();
        return bookMapper.selectByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Book> searchBooks(String keyword, Long userId) {
        return bookMapper.searchBooks(keyword, userId);
    }

    @Override
    public Book getReadingStats(Long userId) {
        return bookMapper.getReadingStats(userId);
    }
}
