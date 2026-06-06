package com.emotional.diary.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class QwenAiService {

    private static final Logger logger = LoggerFactory.getLogger(QwenAiService.class);

    @Value("${qwen.api-key:}")
    private String apiKey;

    @Value("${qwen.model:qwen-max}")
    private String model;

    private Generation generation;

    @PostConstruct
    public void init() {
        Constants.baseHttpApiUrl = "https://dashscope.aliyuncs.com/api/v1";
        this.generation = new Generation();
        logger.info("通义千问AI服务初始化完成，模型：{}", model);
    }

    public JSONObject analyzeEmotion(String content) {
        String systemPrompt = "你是一位专业的心理咨询师。请分析用户提供的情绪内容，返回JSON格式的分析结果。";
        String userPrompt = buildEmotionAnalysisPrompt(content);

        return callWithMessages(systemPrompt, userPrompt);
    }

    public JSONObject assessRisk(String content) {
        String systemPrompt = "你是一位专业的心理健康风险评估专家。请评估内容的心理风险等级，返回JSON格式结果。";
        String userPrompt = buildRiskAssessmentPrompt(content);

        return callWithMessages(systemPrompt, userPrompt);
    }

    public JSONObject generateSolution(String emotionType, String content) {
        String systemPrompt = "你是一位专业的心理健康干预专家。请为用户生成个性化的心理调节方案，返回JSON格式。";
        String userPrompt = buildSolutionGenerationPrompt(emotionType, content);

        return callWithMessages(systemPrompt, userPrompt);
    }

    private String buildEmotionAnalysisPrompt(String content) {
        return String.format(
                "日记内容：%s\n\n" +
                        "请返回以下JSON格式（不要包含其他文字）：\n" +
                        "{\n" +
                        "  \"emotion_type\": \"主要情绪类型（开心/平静/焦虑/悲伤/愤怒/恐惧/其他）\",\n" +
                        "  \"emotion_score\": 情绪强度评分(1-10),\n" +
                        "  \"keywords\": [\"关键词1\", \"关键词2\", \"关键词3\"],\n" +
                        "  \"summary\": \"情绪总结(50字以内)\",\n" +
                        "  \"suggestions\": [\"建议1\", \"建议2\", \"建议3\"]\n" +
                        "}",
                content);
    }

    private String buildRiskAssessmentPrompt(String content) {
        return String.format(
                "内容：%s\n\n" +
                        "请返回以下JSON格式（不要包含其他文字）：\n" +
                        "{\n" +
                        "  \"risk_level\": 风险等级(0-正常, 1-低风险, 2-中风险, 3-高风险),\n" +
                        "  \"risk_factors\": [\"风险因素1\", \"风险因素2\"],\n" +
                        "  \"warning_signs\": [\"预警信号1\", \"预警信号2\"],\n" +
                        "  \"recommendation\": \"建议措施\"\n" +
                        "}",
                content);
    }

    private String buildSolutionGenerationPrompt(String emotionType, String content) {
        return String.format(
                "用户当前情绪状态为：%s，相关描述如下：\n%s\n\n" +
                        "请为该用户生成个性化的心理调节方案，返回JSON格式：\n" +
                        "{\n" +
                        "  \"title\": \"方案标题\",\n" +
                        "  \"methods\": [\n" +
                        "    {\n" +
                        "      \"name\": \"方法名称\",\n" +
                        "      \"description\": \"详细描述\",\n" +
                        "      \"duration_minutes\": 建议时长,\n" +
                        "      \"difficulty\": \"难度(初级/中级/高级)\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"daily_tips\": [\"每日建议1\", \"每日建议2\"],\n" +
                        "  \"resources\": [\"推荐资源1\", \"推荐资源2\"]\n" +
                        "}",
                emotionType, content);
    }

    private JSONObject callWithMessages(String systemPrompt, String userPrompt) {
        try {
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemPrompt)
                    .build();

            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userPrompt)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .temperature(0.7f)
                    .maxTokens(2000)
                    .build();

            logger.info("开始调用通义千问API，模型：{}", model);
            GenerationResult result = generation.call(param);

            String responseContent = result.getOutput()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            logger.info("AI服务调用成功，响应长度：{}字符", responseContent.length());

            return JSON.parseObject(responseContent.trim());

        } catch (NoApiKeyException e) {
            logger.error("API Key未配置", e);
            throw new RuntimeException("AI服务异常：未配置有效的API Key");
        } catch (ApiException e) {
            logger.error("API调用失败：{}", e.getMessage(), e);
            throw new RuntimeException("AI服务异常：" + e.getMessage());
        } catch (InputRequiredException e) {
            logger.error("输入参数错误", e);
            throw new RuntimeException("AI服务异常：" + e.getMessage());
        } catch (Exception e) {
            logger.error("AI服务未知异常", e);
            throw new RuntimeException("AI服务异常：" + e.getMessage());
        }
    }
}
