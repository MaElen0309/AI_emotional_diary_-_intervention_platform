package com.emotional.diary.mapper;

import com.emotional.diary.entity.TreeholeContent;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TreeholeContentMapper {

    @Select("SELECT * FROM treehole_content WHERE id = #{id}")
    TreeholeContent findById(Long id);

    @Select("SELECT * FROM treehole_content WHERE audit_status = #{auditStatus} ORDER BY is_top DESC, create_time DESC LIMIT #{offset}, #{pageSize}")
    List<TreeholeContent> findByAuditStatus(@Param("auditStatus") Integer auditStatus, 
                                            @Param("offset") int offset, 
                                            @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM treehole_content WHERE audit_status = #{auditStatus}")
    int countByAuditStatus(Integer auditStatus);

    @Insert("INSERT INTO treehole_content (user_id, anonymous_name, title, content, category_id, images) " +
            "VALUES (#{userId}, #{anonymousName}, #{title}, #{content}, #{categoryId}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TreeholeContent content);

    @Update("UPDATE treehole_content SET audit_status=#{auditStatus}, audit_user_id=#{auditUserId}, audit_remark=#{auditRemark}, audit_time=NOW() WHERE id=#{id}")
    int updateAudit(@Param("id") Long id, 
                    @Param("auditStatus") Integer auditStatus,
                    @Param("auditUserId") Long auditUserId, 
                    @Param("auditRemark") String auditRemark);

    @Update("UPDATE treehole_content SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE treehole_content SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Delete("DELETE FROM treehole_content WHERE id = #{id}")
    int deleteById(Long id);
}
