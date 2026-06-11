package com.emotional.diary.service;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.entity.TreeholeComment;
import com.emotional.diary.entity.TreeholeContent;
import com.emotional.diary.entity.TreeholeFavorite;
import com.emotional.diary.mapper.TreeholeCommentMapper;
import com.emotional.diary.mapper.TreeholeContentMapper;
import com.emotional.diary.mapper.TreeholeFavoriteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TreeholeService {

    private static final Logger logger = LoggerFactory.getLogger(TreeholeService.class);

    @Resource
    private TreeholeContentMapper contentMapper;

    @Resource
    private TreeholeCommentMapper commentMapper;

    @Resource
    private TreeholeFavoriteMapper favoriteMapper;

    public TreeholeContent getContentById(Long id) {
        TreeholeContent content = contentMapper.findById(id);
        if (content != null && content.getAuditStatus() == TreeholeContent.AUDIT_APPROVED) {
            contentMapper.incrementViewCount(id);
        }
        return content;
    }

    public PageInfo<TreeholeContent> getPendingList(PageRequest pageRequest) {
        List<TreeholeContent> list = contentMapper.findByAuditStatus(
                TreeholeContent.AUDIT_PENDING, pageRequest.getOffset(), pageRequest.getPageSize());
        int total = contentMapper.countByAuditStatus(TreeholeContent.AUDIT_PENDING);
        return new PageInfo<>(list, total, pageRequest);
    }

    public PageInfo<TreeholeContent> getApprovedList(PageRequest pageRequest) {
        List<TreeholeContent> list = contentMapper.findByAuditStatus(
                TreeholeContent.AUDIT_APPROVED, pageRequest.getOffset(), pageRequest.getPageSize());
        int total = contentMapper.countByAuditStatus(TreeholeContent.AUDIT_APPROVED);
        return new PageInfo<>(list, total, pageRequest);
    }

    public void publishContent(TreeholeContent content) {
        content.setAuditStatus(TreeholeContent.AUDIT_PENDING);
        contentMapper.insert(content);
    }

    public void auditContent(Long contentId, Long auditorId, Integer status, String remark) {
        contentMapper.updateAudit(contentId, status, auditorId, remark);
    }

    public void likeContent(Long contentId) {
        contentMapper.incrementLikeCount(contentId);
    }

    public void deleteContent(Long contentId) {
        contentMapper.deleteById(contentId);
    }

    // ========== 评论相关方法 ==========

    public PageInfo<TreeholeComment> getComments(Long contentId, PageRequest pageRequest) {
        List<TreeholeComment> list = commentMapper.findByContentId(contentId,
                pageRequest.getOffset(), pageRequest.getPageSize());
        int total = commentMapper.countByContentId(contentId);

        // 查询每个评论的回复
        for (TreeholeComment comment : list) {
            List<TreeholeComment> replies = commentMapper.findReplies(comment.getId());
            comment.setReplies(replies);
        }

        return new PageInfo<>(list, total, pageRequest);
    }

    public TreeholeComment addComment(TreeholeComment comment) {
        logger.info("Service层 - 接收评论对象: contentId={}, userId={}, content='{}', anonymousName='{}', parentId={}",
                comment.getContentId(), comment.getUserId(), comment.getContent(),
                comment.getAnonymousName(), comment.getParentId());
        commentMapper.insert(comment);
        commentMapper.incrementCommentCount(comment.getContentId());
        return comment;
    }

    public void likeComment(Long commentId) {
        commentMapper.incrementLikeCount(commentId);
    }

    public void deleteComment(Long commentId, Long contentId) {
        commentMapper.deleteById(commentId);
        commentMapper.decrementCommentCount(contentId);
    }

    // ========== 收藏相关方法 ==========

    public boolean isFavorited(Long contentId, Long userId) {
        return favoriteMapper.findByContentAndUser(contentId, userId) != null;
    }

    public int getFavoriteCount(Long contentId) {
        return favoriteMapper.countByContentId(contentId);
    }

    public void addFavorite(TreeholeFavorite favorite) {
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long contentId, Long userId) {
        favoriteMapper.deleteByContentAndUser(contentId, userId);
    }
}
