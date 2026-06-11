package com.emotional.diary.mapper;

import com.emotional.diary.entity.TreeholeFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TreeholeFavoriteMapper {

    @Select("SELECT * FROM treehole_favorite WHERE content_id = #{contentId} AND user_id = #{userId}")
    TreeholeFavorite findByContentAndUser(@Param("contentId") Long contentId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM treehole_favorite WHERE content_id = #{contentId}")
    int countByContentId(Long contentId);

    @Select("SELECT * FROM treehole_favorite WHERE user_id = #{userId}")
    List<TreeholeFavorite> findByUserId(Long userId);

    @Insert("INSERT INTO treehole_favorite (content_id, user_id) VALUES (#{contentId}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TreeholeFavorite favorite);

    @Delete("DELETE FROM treehole_favorite WHERE content_id = #{contentId} AND user_id = #{userId}")
    int deleteByContentAndUser(@Param("contentId") Long contentId, @Param("userId") Long userId);
}
