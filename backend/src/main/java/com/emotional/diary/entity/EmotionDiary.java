package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmotionDiary {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer emotionType;
    private Integer emotionScore;
    private String aiAnalysisResult;
    private Integer riskLevel;
    private Integer isPrivate;
    private Integer likeCount;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static final int EMOTION_HAPPY = 1;
    public static final int EMOTION_CALM = 2;
    public static final int EMOTION_ANXIOUS = 3;
    public static final int EMOTION_SAD = 4;
    public static final int EMOTION_ANGRY = 5;
    public static final int EMOTION_FEAR = 6;
    public static final int EMOTION_OTHER = 7;

    public static final int RISK_NORMAL = 0;
    public static final int RISK_LOW = 1;
    public static final int RISK_MEDIUM = 2;
    public static final int RISK_HIGH = 3;
}
