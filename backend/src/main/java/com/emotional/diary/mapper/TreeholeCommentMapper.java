package com.emotional.diary.mapper;

import com.emotional.diary.entity.TreeholeComment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TreeholeCommentMapper {

        @Select("SELECT * FROM treehole_comment WHERE id = #{id}")
        TreeholeComment findById(Long id);

        @Select("SELECT c.*, u.nickname AS userAnonymousName FROM treehole_comment c " +
                        "LEFT JOIN sys_user u ON c.user_id = u.id " +
                        "WHERE c.content_id = #{contentId} AND (c.parent_id IS NULL OR c.parent_id = 0) " +
                        "ORDER BY c.create_time ASC LIMIT #{offset}, #{pageSize}")
        List<TreeholeComment> findByContentId(@Param("contentId") Long contentId,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

        @Select("SELECT COUNT(*) FROM treehole_comment WHERE content_id = #{contentId} AND (parent_id IS NULL OR parent_id = 0)")
        int countByContentId(Long contentId);

        @Select("SELECT c.*, u.nickname AS userAnonymousName FROM treehole_comment c " +
                        "LEFT JOIN sys_user u ON c.user_id = u.id " +
                        "WHERE c.parent_id = #{parentId} ORDER BY c.create_time ASC")
        List<TreeholeComment> findReplies(Long parentId);

        @Insert("INSERT INTO treehole_comment (content_id, user_id, anonymous_name, content, parent_id) " +
                        "VALUES (#{contentId}, #{userId}, #{anonymousName}, #{content}, #{parentId})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(TreeholeComment comment);

        @Update("UPDATE treehole_content SET comment_count = comment_count + 1 WHERE id = #{contentId}")
        int incrementCommentCount(Long contentId);

        @Update("UPDATE treehole_content SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{contentId}")
        int decrementCommentCount(Long contentId);

        @Update("UPDATE treehole_comment SET like_count = like_count + 1 WHERE id = #{id}")
        int incrementLikeCount(Long id);

        @Delete("DELETE FROM treehole_comment WHERE id = #{id}")
        int deleteById(Long id);
}
