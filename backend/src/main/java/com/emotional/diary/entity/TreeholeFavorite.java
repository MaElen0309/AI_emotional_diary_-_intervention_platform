package com.emotional.diary.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TreeholeFavorite {
    private Long id;
    private Long contentId;  // 树洞帖子ID
    private Long userId;     // 收藏用户ID
    private LocalDateTime createTime;
}
