package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.SysUser;
import com.emotional.diary.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping
    public Result<PageInfo<SysUser>> getList(PageRequest pageRequest) {
        PageInfo<SysUser> pageInfo = userService.getUserList(pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        userService.updateUserStatus(id, params.get("status"));
        return Result.success("状态更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}
