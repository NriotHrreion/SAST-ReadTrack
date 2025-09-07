package com.sast.sastreadtrack.service.impl;

import com.sast.sastreadtrack.entity.User;
import com.sast.sastreadtrack.mapper.BookMapper;
import com.sast.sastreadtrack.mapper.UserMapper;
import com.sast.sastreadtrack.service.UserService;
import com.sast.sastreadtrack.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    @Transactional
    public boolean register(User user) {
        if(userMapper.selectByName(user.getUsername()) != null) return false;
        user.setPassword(encoder.encode(user.getPassword()));
        userMapper.insert(user);
        return true;
    }

    @Override
    public String login(String username, String password) {
        User user = userMapper.selectByName(username);
        if(user == null) return null;

        final String realPassword = user.getPassword();
        if(!encoder.matches(password, realPassword)) {
            return null;
        }

        return MD5.hash(user.getUsername() + realPassword + getCurrentDateString());
    }

    @Override
    public boolean authToken(Long id, String token) {
        User user = userMapper.selectById(id);
        if(user == null) return false;

        final String realPassword = user.getPassword();
        final String realToken = MD5.hash(user.getUsername() + realPassword + getCurrentDateString());
        return token.equals(realToken);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.selectByName(username);
    }

    private String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
        return dateFormat.format(new Date());
    }
}
