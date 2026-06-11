package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.TreeholeComment;
import com.emotional.diary.entity.TreeholeContent;
import com.emotional.diary.entity.TreeholeFavorite;
import com.emotional.diary.service.TreeholeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/treehole")
public class TreeholeController {

    private static final Logger logger = LoggerFactory.getLogger(TreeholeController.class);

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
    public Result<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> params,
            HttpServletRequest request) {
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

    // ========== 评论接口 ==========

    @GetMapping("/{contentId}/comments")
    public Result<PageInfo<TreeholeComment>> getComments(@PathVariable Long contentId, PageRequest pageRequest) {
        PageInfo<TreeholeComment> pageInfo = treeholeService.getComments(contentId, pageRequest);
        return Result.success(pageInfo);
    }

    @PostMapping("/{contentId}/comments")
    public Result<TreeholeComment> addComment(@PathVariable Long contentId,
            @RequestBody TreeholeComment comment,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        logger.info("收到评论请求: contentId={}, userId={}, content='{}', anonymousName='{}', parentId={}",
                contentId, userId, comment.getContent(), comment.getAnonymousName(), comment.getParentId());
        comment.setContentId(contentId);
        comment.setUserId(userId);
        TreeholeComment saved = treeholeService.addComment(comment);
        return Result.success(saved);
    }

    @PostMapping("/comments/{commentId}/like")
    public Result<String> likeComment(@PathVariable Long commentId) {
        treeholeService.likeComment(commentId);
        return Result.success("点赞成功");
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId,
            @RequestParam("contentId") Long contentId) {
        treeholeService.deleteComment(commentId, contentId);
        return Result.success("删除评论成功");
    }

    // ========== 收藏接口 ==========

    @GetMapping("/{contentId}/favorite/status")
    public Result<Map<String, Object>> getFavoriteStatus(@PathVariable Long contentId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        Map<String, Object> result = new HashMap<>();

        // 未登录时返回默认值
        if (userId == null) {
            result.put("isFavorited", false);
            result.put("count", 0);
            return Result.success(result);
        }

        boolean isFavorited = treeholeService.isFavorited(contentId, userId);
        int count = treeholeService.getFavoriteCount(contentId);

        result.put("isFavorited", isFavorited);
        result.put("count", count);
        return Result.success(result);
    }

    @PostMapping("/{contentId}/favorite")
    public Result<String> addFavorite(@PathVariable Long contentId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");

        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        // 检查是否已收藏
        if (treeholeService.isFavorited(contentId, userId)) {
            treeholeService.removeFavorite(contentId, userId);
            return Result.success("取消收藏成功");
        }

        TreeholeFavorite favorite = new TreeholeFavorite();
        favorite.setContentId(contentId);
        favorite.setUserId(userId);
        treeholeService.addFavorite(favorite);
        return Result.success("收藏成功");
    }
}
