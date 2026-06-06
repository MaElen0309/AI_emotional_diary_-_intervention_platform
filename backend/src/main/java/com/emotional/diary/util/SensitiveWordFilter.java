package com.emotional.diary.util;

import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.Set;

public class SensitiveWordFilter {
    private static Set<String> sensitiveWords = new HashSet<>();

    static {
        sensitiveWords.add("自杀");
        sensitiveWords.add("去死");
        sensitiveWords.add("杀人");
        sensitiveWords.add("暴力");
        sensitiveWords.add("毒品");
        sensitiveWords.add("赌博");
        sensitiveWords.add("色情");
        // 敏感词库可根据需要扩展
    }

    public static boolean containsSensitiveWord(String text) {
        if (!StringUtils.hasText(text)) return false;
        
        String lowerText = text.toLowerCase();
        for (String word : sensitiveWords) {
            if (lowerText.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String filter(String text) {
        if (!StringUtils.hasText(text)) return text;
        
        String result = text;
        for (String word : sensitiveWords) {
            result = result.replaceAll("(?i)" + word, "***");
        }
        return result;
    }

    public static void addWord(String word) {
        if (StringUtils.hasText(word)) {
            sensitiveWords.add(word.trim());
        }
    }
}
