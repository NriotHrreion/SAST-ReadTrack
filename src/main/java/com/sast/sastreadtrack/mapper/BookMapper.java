package com.sast.sastreadtrack.mapper;

import com.sast.sastreadtrack.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 书籍数据访问层
 */
@Mapper
public interface BookMapper {
    /**
     * 新增书籍
     */
    @Insert("INSERT INTO book (user_id, title, author, total_pages, current_page) VALUES (#{userId}, #{title}, #{author}, #{totalPages}, #{currentPage})")
    int insert(Book book);

    /**
     * 更新阅读进度
     */
    @Update("UPDATE book SET current_page=#{currentPage}, status=#{status} WHERE id=#{id} AND user_id=#{userId}")
    int updateReadProgress(Book book);

    /**
     * 更新阅读状态
     * @param book 包含 bookId（id）、userId、status 的实体类
     */
    @Update("UPDATE book SET status=#{status} WHERE id=#{id} AND user_id=#{userId}")
    int updateStatus(Book book);

    /**
     * 删除书籍
     */
    @Delete("DELETE FROM book WHERE id=#{id} AND user_id=#{userId}")
    int deleteById(Long id, Long userId);

    /**
     * 查询用户的所有书籍
     */
    @Select("SELECT id, user_id, title, author, total_pages, current_page, status FROM book WHERE user_id=#{userId}")
    List<Book> selectByUserId(Long userId);

    /**
     * 查询用户的某本书籍
     */
    @Select("SELECT id, user_id, title, author, total_pages, current_page, status FROM book WHERE user_id=#{userId} AND id=#{bookId}")
    Book selectByUserIdAndBookId(Long userId, Long bookId);

    /**
     * 根据阅读状态查询用户的书籍
     */
    @Select("SELECT id, user_id, title, author, total_pages, current_page, status FROM book WHERE user_id=#{userId} AND status=#{status}")
    List<Book> selectByUserIdAndStatus(Long userId, String status);

    /**
     * 模糊查询书籍
     */
    @Select("SELECT id, user_id, title, author, total_pages, current_page, status FROM book WHERE user_id=#{userId} AND (title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%'))")
    List<Book> searchBooks(String keyword, Long userId);

    /**
     * 获取用户的阅读统计
     */
    @Select("SELECT SUM(current_page) as current_page, SUM(total_pages) as total_pages FROM book WHERE user_id=#{userId}")
    Book getReadingStats(Long userId);
}
