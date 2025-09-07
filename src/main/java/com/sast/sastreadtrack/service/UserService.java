package com.sast.sastreadtrack.service;

import com.sast.sastreadtrack.entity.User;
import org.springframework.stereotype.Service;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 用户注册
     * @param user 用户信息（包含姓名、密码）
     * @return 注册成功返回true
     */
    boolean register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回会话token
     */
    String login(String username, String password);

    /**
     * 会话token验证
     * @param id
     * @param token
     * @return
     */
    boolean authToken(Long id, String token);

    /**
     * 根据id获取用户信息
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户信息
     */
    User getUserByName(String username);
}
