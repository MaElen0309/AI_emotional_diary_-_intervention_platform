package com.emotional.diary.service;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.entity.TreeholeContent;
import com.emotional.diary.mapper.TreeholeContentMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class TreeholeService {

    @Resource
    private TreeholeContentMapper contentMapper;

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
}
