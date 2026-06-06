package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.EmotionDiary;
import com.emotional.diary.service.EmotionDiaryService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/diary")
public class EmotionDiaryController {

    @Resource
    private EmotionDiaryService diaryService;

    @GetMapping
    public Result<PageInfo<EmotionDiary>> getList(PageRequest pageRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        PageInfo<EmotionDiary> pageInfo = diaryService.getDiaryList(userId, pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<EmotionDiary> getById(@PathVariable Long id) {
        EmotionDiary diary = diaryService.getDiaryById(id);
        return Result.success(diary);
    }

    @PostMapping
    public Result<String> create(@RequestBody EmotionDiary diary, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        diary.setUserId(userId);
        diaryService.createDiary(diary);
        return Result.success("日记创建成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody EmotionDiary diary) {
        diary.setId(id);
        diaryService.updateDiary(diary);
        return Result.success("日记更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return Result.success("日记删除成功");
    }
}
