package com.emotional.diary.controller;

import com.emotional.diary.common.Result;
import com.emotional.diary.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/admin/config")
public class SystemConfigController {

    @Resource
    private SystemConfigService configService;

    @GetMapping
    public Result<Map<String, String>> getAll() {
        Map<String, String> configs = configService.getAllConfigs();
        return Result.success(configs);
    }

    @GetMapping("/{key}")
    public Result<String> getByKey(@PathVariable String key) {
        String value = configService.getConfigValue(key);
        return Result.success(value);
    }

    @PutMapping
    public Result<String> update(@RequestBody Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            configService.updateConfig(entry.getKey(), entry.getValue());
        }
        return Result.success("配置更新成功");
    }
}
