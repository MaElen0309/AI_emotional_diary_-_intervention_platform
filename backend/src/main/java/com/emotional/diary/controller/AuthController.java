package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.SysUser;
import com.emotional.diary.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");
        
        try {
            SysUser user = userService.login(username, password);
            session.setAttribute("currentUser", user);
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }

    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error(401, "未登录");
    }

    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> params, HttpSession session) {
        Long userId = (Long) session.getAttribute("currentUserId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
