package com.emotional.diary.service;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.entity.EmotionDiary;
import com.emotional.diary.mapper.EmotionDiaryMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class EmotionDiaryService {

    @Resource
    private EmotionDiaryMapper diaryMapper;

    public EmotionDiary getDiaryById(Long id) {
        EmotionDiary diary = diaryMapper.findById(id);
        if (diary != null) {
            diaryMapper.incrementViewCount(id);
        }
        return diary;
    }

    public PageInfo<EmotionDiary> getDiaryList(Long userId, PageRequest pageRequest) {
        List<EmotionDiary> list;
        int total;

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            list = diaryMapper.searchByKeyword(userId, pageRequest.getKeyword(), pageRequest.getOffset(), pageRequest.getPageSize());
            total = diaryMapper.countSearchByKeyword(userId, pageRequest.getKeyword());
        } else {
            list = diaryMapper.findByUserId(userId, pageRequest.getOffset(), pageRequest.getPageSize());
            total = diaryMapper.countByUserId(userId);
        }

        return new PageInfo<>(list, total, pageRequest);
    }

    public void createDiary(EmotionDiary diary) {
        if (diary.getIsPrivate() == null) {
            diary.setIsPrivate(1);
        }
        diaryMapper.insert(diary);
    }

    public void updateDiary(EmotionDiary diary) {
        diaryMapper.update(diary);
    }

    public void deleteDiary(Long id) {
        diaryMapper.deleteById(id);
    }

    public void updateAiAnalysis(Long id, String analysisResult, Integer riskLevel) {
        diaryMapper.updateAiAnalysis(id, analysisResult, riskLevel);
    }
}
