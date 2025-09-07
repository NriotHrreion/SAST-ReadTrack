package com.sast.sastreadtrack.mapper;

import com.sast.sastreadtrack.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {
    /**
     * 新增用户
     */
    @Insert("INSERT INTO t_user (username, password) VALUES (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Long insert(User user);

    /**
     * 根据id查询用户
     */
    @Select("SELECT id, username, password, created_at FROM t_user WHERE id=#{id}")
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT id, username, password, created_at FROM t_user WHERE username=#{username}")
    User selectByName(String username);
}
