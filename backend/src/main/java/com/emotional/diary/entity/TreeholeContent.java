package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TreeholeContent {
    private Long id;
    private Long userId;
    private String anonymousName;
    private String title;
    private String content;
    private Long categoryId;
    private String images;
    private Integer auditStatus;
    private Long auditUserId;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private Integer isTop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int AUDIT_PENDING = 0;
    public static final int AUDIT_APPROVED = 1;
    public static final int AUDIT_REJECTED = 2;
}
