package com.emotional.diary.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.EmotionDiary;
import com.emotional.diary.mapper.EmotionDiaryMapper;
import com.emotional.diary.service.EmotionDiaryService;
import com.emotional.diary.service.QwenAiService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private QwenAiService qwenAiService;

    @Resource
    private EmotionDiaryService diaryService;

    @Resource
    private EmotionDiaryMapper diaryMapper;

    @PostMapping("/analyze/diary/{diaryId}")
    public Result<JSONObject> analyzeDiary(@PathVariable Long diaryId) {
        EmotionDiary diary = diaryService.getDiaryById(diaryId);
        if (diary == null) {
            return Result.error("日记不存在");
        }

        try {
            JSONObject emotionResult = qwenAiService.analyzeEmotion(diary.getContent());
            JSONObject riskResult = qwenAiService.assessRisk(diary.getContent());

            JSONObject combinedResult = new JSONObject();
            combinedResult.put("emotion_analysis", emotionResult);
            combinedResult.put("risk_assessment", riskResult);

            diaryMapper.updateAiAnalysis(diaryId, 
                combinedResult.toJSONString(), 
                riskResult.getIntValue("risk_level"));

            return Result.success(combinedResult);
        } catch (Exception e) {
            return Result.error("AI分析失败：" + e.getMessage());
        }
    }

    @PostMapping("/analyze/content")
    public Result<JSONObject> analyzeContent(@RequestBody Map<String, String> params) {
        String content = params.get("content");
        
        try {
            JSONObject emotionResult = qwenAiService.analyzeEmotion(content);
            JSONObject riskResult = qwenAiService.assessRisk(content);

            JSONObject combinedResult = new JSONObject();
            combinedResult.put("emotion_analysis", emotionResult);
            combinedResult.put("risk_assessment", riskResult);

            return Result.success(combinedResult);
        } catch (Exception e) {
            return Result.error("AI分析失败：" + e.getMessage());
        }
    }

    @PostMapping("/generate/solution")
    public Result<JSONObject> generateSolution(@RequestBody Map<String, String> params) {
        String emotionType = params.get("emotionType");
        String content = params.get("content");

        try {
            JSONObject solution = qwenAiService.generateSolution(emotionType, content);
            return Result.success(solution);
        } catch (Exception e) {
            return Result.error("方案生成失败：" + e.getMessage());
        }
    }
}
