package com.emotional.diary.controller;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.common.Result;
import com.emotional.diary.entity.Solution;
import com.emotional.diary.service.SolutionService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/solutions")
public class SolutionController {

    @Resource
    private SolutionService solutionService;

    @GetMapping
    public Result<PageInfo<Solution>> getList(PageRequest pageRequest) {
        PageInfo<Solution> pageInfo = solutionService.getPublishedList(pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/admin")
    public Result<PageInfo<Solution>> getAdminList(PageRequest pageRequest) {
        PageInfo<Solution> pageInfo = solutionService.getAllList(pageRequest);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<Solution> getById(@PathVariable Long id) {
        Solution solution = solutionService.getSolutionById(id);
        return Result.success(solution);
    }

    @PostMapping
    public Result<String> create(@RequestBody Solution solution) {
        solutionService.createSolution(solution);
        return Result.success("方案创建成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Solution solution) {
        solution.setId(id);
        solutionService.updateSolution(solution);
        return Result.success("方案更新成功");
    }

    @PutMapping("/{id}/publish")
    public Result<String> publish(@PathVariable Long id) {
        solutionService.publishSolution(id);
        return Result.success("上架成功");
    }

    @PutMapping("/{id}/offline")
    public Result<String> offline(@PathVariable Long id) {
        solutionService.offlineSolution(id);
        return Result.success("下架成功");
    }

    @PostMapping("/{id}/use")
    public Result<String> use(@PathVariable Long id) {
        solutionService.useSolution(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<String> like(@PathVariable Long id) {
        solutionService.likeSolution(id);
        return Result.success("点赞成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        solutionService.deleteSolution(id);
        return Result.success("删除成功");
    }
}
