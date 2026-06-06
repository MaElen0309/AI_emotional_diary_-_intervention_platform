package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Solution {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String audioUrl;
    private String category;
    private String targetEmotion;
    private Integer difficultyLevel;
    private Integer durationMinutes;
    private Long authorId;
    private Integer status;
    private Integer useCount;
    private Integer likeCount;
    private Double rating;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_OFFLINE = 2;
}
