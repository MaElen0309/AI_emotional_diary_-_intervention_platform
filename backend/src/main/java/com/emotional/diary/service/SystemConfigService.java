package com.emotional.diary.service;

import com.emotional.diary.mapper.SystemConfigMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigService {

    @Resource
    private SystemConfigMapper configMapper;

    public String getConfigValue(String key) {
        Map<String, Object> config = configMapper.findByKey(key);
        if (config != null) {
            return (String) config.get("config_value");
        }
        return null;
    }

    public Map<String, String> getAllConfigs() {
        List<Map<String, Object>> list = configMapper.findAll();
        Map<String, String> result = new HashMap<>();
        for (Map<String, Object> item : list) {
            result.put((String) item.get("config_key"), (String) item.get("config_value"));
        }
        return result;
    }

    public void updateConfig(String key, String value) {
        Map<String, Object> existing = configMapper.findByKey(key);
        if (existing != null) {
            configMapper.update(key, value, (String) existing.get("description"));
        } else {
            Map<String, Object> newConfig = new HashMap<>();
            newConfig.put("configKey", key);
            newConfig.put("configValue", value);
            newConfig.put("configType", "string");
            newConfig.put("description", "");
            configMapper.insert(newConfig);
        }
    }
}
