package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TreeholeComment {
    private Long id;
    private Long contentId; // 树洞帖子ID
    private Long userId; // 评论用户ID
    private String anonymousName;// 匿名昵称
    private String content; // 评论内容
    private Long parentId; // 父评论ID（用于回复）
    private Integer likeCount; // 点赞数
    private LocalDateTime createTime;

    // 带用户信息的扩展字段（查询时使用）
    private String userAnonymousName; // 评论者昵称
    private List<TreeholeComment> replies; // 回复列表
}
