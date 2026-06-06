package com.emotional.diary.service;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.entity.SysUser;
import com.emotional.diary.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    @Resource
    private SysUserMapper userMapper;

    private static final String SALT = "emotional_diary_2024";

    /** SHA-256 + 盐值加密 */
    private String encodePassword(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((SALT + rawPassword).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    public SysUser login(String username, String password) {
        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!encodePassword(password).equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        return user;
    }

    public void register(SysUser user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(encodePassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(SysUser.ROLE_STUDENT);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);
    }

    public SysUser getUserById(Long id) {
        return userMapper.findById(id);
    }

    public PageInfo<SysUser> getUserList(PageRequest pageRequest) {
        String keyword = pageRequest.getKeyword() != null ? pageRequest.getKeyword() : "";
        List<SysUser> list = userMapper.findByKeyword(keyword, pageRequest.getOffset(), pageRequest.getPageSize());
        int total = userMapper.countByKeyword(keyword);
        return new PageInfo<>(list, total, pageRequest);
    }

    public void updateUser(SysUser user) {
        userMapper.update(user);
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!encodePassword(oldPassword).equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        userMapper.updatePassword(userId, encodePassword(newPassword));
    }

    public void updateUserStatus(Long userId, Integer status) {
        userMapper.updateStatus(userId, status);
    }

    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    public boolean checkUsernameExists(String username) {
        return userMapper.findByUsername(username) != null;
    }
}
