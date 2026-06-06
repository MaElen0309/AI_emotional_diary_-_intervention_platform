package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.TreeholeContent;
import com.emotional.diary.service.TreeholeService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/treehole")
public class TreeholeController {

    @Resource
    private TreeholeService treeholeService;

    @GetMapping("/published")
    public Result<PageInfo<TreeholeContent>> getPublishedList(PageRequest pageRequest) {
        PageInfo<TreeholeContent> pageInfo = treeholeService.getApprovedList(pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/pending")
    public Result<PageInfo<TreeholeContent>> getPendingList(PageRequest pageRequest) {
        PageInfo<TreeholeContent> pageInfo = treeholeService.getPendingList(pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<TreeholeContent> getById(@PathVariable Long id) {
        TreeholeContent content = treeholeService.getContentById(id);
        return Result.success(content);
    }

    @PostMapping
    public Result<String> publish(@RequestBody TreeholeContent content, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        content.setUserId(userId);
        treeholeService.publishContent(content);
        return Result.success("发布成功，等待审核");
    }

    @PutMapping("/{id}/audit")
    public Result<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long auditorId = (Long) request.getAttribute("currentUserId");
        Integer status = (Integer) params.get("status");
        String remark = (String) params.get("remark");
        
        treeholeService.auditContent(id, auditorId, status, remark);
        
        if (status == TreeholeContent.AUDIT_APPROVED) {
            return Result.success("审核通过");
        } else {
            return Result.success("审核拒绝");
        }
    }

    @PostMapping("/{id}/like")
    public Result<String> like(@PathVariable Long id) {
        treeholeService.likeContent(id);
        return Result.success("点赞成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        treeholeService.deleteContent(id);
        return Result.success("删除成功");
    }
}
