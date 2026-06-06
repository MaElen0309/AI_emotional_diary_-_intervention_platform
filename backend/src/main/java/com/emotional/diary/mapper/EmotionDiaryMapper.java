package com.emotional.diary.mapper;

import com.emotional.diary.entity.EmotionDiary;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface EmotionDiaryMapper {

    @Select("SELECT * FROM emotion_diary WHERE id = #{id}")
    EmotionDiary findById(Long id);

    @Select("SELECT * FROM emotion_diary WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<EmotionDiary> findByUserId(@Param("userId") Long userId, 
                                    @Param("offset") int offset, 
                                    @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM emotion_diary WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Select("SELECT * FROM emotion_diary WHERE user_id = #{userId} AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<EmotionDiary> searchByKeyword(@Param("userId") Long userId, 
                                       @Param("keyword") String keyword,
                                       @Param("offset") int offset, 
                                       @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM emotion_diary WHERE user_id = #{userId} AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%'))")
    int countSearchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Insert("INSERT INTO emotion_diary (user_id, title, content, emotion_type, emotion_score, is_private) " +
            "VALUES (#{userId}, #{title}, #{content}, #{emotionType}, #{emotionScore}, #{isPrivate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmotionDiary diary);

    @Update("UPDATE emotion_diary SET title=#{title}, content=#{content}, emotion_type=#{emotionType}, emotion_score=#{emotionScore}, is_private=#{isPrivate}, ai_analysis_result=#{aiAnalysisResult}, risk_level=#{riskLevel} WHERE id=#{id}")
    int update(EmotionDiary diary);

    @Update("UPDATE emotion_diary SET ai_analysis_result=#{aiAnalysisResult}, risk_level=#{riskLevel} WHERE id=#{id}")
    int updateAiAnalysis(@Param("id") Long id, @Param("aiAnalysisResult") String aiAnalysisResult, @Param("riskLevel") Integer riskLevel);

    @Update("UPDATE emotion_diary SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Delete("DELETE FROM emotion_diary WHERE id = #{id}")
    int deleteById(Long id);
}
