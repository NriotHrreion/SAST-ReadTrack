package com.sast.sastreadtrack.controller;

import com.sast.sastreadtrack.entity.User;
import com.sast.sastreadtrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 * 处理用户注册、登录等请求
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param user 用户信息（包含 、姓名、密码）
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        if(!userService.register(user)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 用户登录
     * @param loginForm 登录表单（包含 、密码）
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        if(!loginForm.containsKey("username") || !loginForm.containsKey("password")) {
            return ResponseEntity.badRequest().build();
        }

        final String username = loginForm.get("username");
        final String password = loginForm.get("password");
        String token = userService.login(username, password);
        if(token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getUserByName(username);
        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("id", user.getId());
        return ResponseEntity.of(Optional.of(res));
    }
}
