package com.emotional.diary.mapper;

import com.emotional.diary.entity.Solution;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SolutionMapper {

    @Select("SELECT * FROM solution WHERE id = #{id}")
    Solution findById(Long id);

    @Select("<script>" +
            "SELECT * FROM solution " +
            "<if test='status != null'>WHERE status = #{status}</if>" +
            "<if test='status == null'></if> " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Solution> findByStatus(@Param("status") Integer status, 
                                @Param("offset") int offset, 
                                @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM solution " +
            "<if test='status != null'>WHERE status = #{status}</if>" +
            "<if test='status == null'></if>" +
            "</script>")
    int countByStatus(Integer status);

    @Insert("INSERT INTO solution (title, summary, content, cover_image, audio_url, category, target_emotion, difficulty_level, duration_minutes, author_id, status) " +
            "VALUES (#{title}, #{summary}, #{content}, #{coverImage}, #{audioUrl}, #{category}, #{targetEmotion}, #{difficultyLevel}, #{durationMinutes}, #{authorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Solution solution);

    @Update("UPDATE solution SET title=#{title}, summary=#{summary}, content=#{content}, cover_image=#{coverImage}, audio_url=#{audioUrl}, category=#{category}, target_emotion=#{targetEmotion}, difficulty_level=#{difficultyLevel}, duration_minutes=#{durationMinutes} WHERE id=#{id}")
    int update(Solution solution);

    @Update("UPDATE solution SET status=#{status}, publish_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE solution SET use_count = use_count + 1 WHERE id = #{id}")
    int incrementUseCount(Long id);

    @Update("UPDATE solution SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Delete("DELETE FROM solution WHERE id = #{id}")
    int deleteById(Long id);
}
