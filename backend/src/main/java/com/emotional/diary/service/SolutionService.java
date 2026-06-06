package com.emotional.diary.service;

import com.emotional.diary.common.PageInfo;
import com.emotional.diary.common.PageRequest;
import com.emotional.diary.entity.Solution;
import com.emotional.diary.mapper.SolutionMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class SolutionService {

    @Resource
    private SolutionMapper solutionMapper;

    public Solution getSolutionById(Long id) {
        return solutionMapper.findById(id);
    }

    public PageInfo<Solution> getPublishedList(PageRequest pageRequest) {
        List<Solution> list = solutionMapper.findByStatus(
            Solution.STATUS_PUBLISHED, pageRequest.getOffset(), pageRequest.getPageSize());
        int total = solutionMapper.countByStatus(Solution.STATUS_PUBLISHED);
        return new PageInfo<>(list, total, pageRequest);
    }

    public PageInfo<Solution> getAllList(PageRequest pageRequest) {
        List<Solution> list = solutionMapper.findByStatus(null, pageRequest.getOffset(), pageRequest.getPageSize());
        int total = solutionMapper.countByStatus(null);
        return new PageInfo<>(list, total, pageRequest);
    }

    public void createSolution(Solution solution) {
        if (solution.getStatus() == null) {
            solution.setStatus(Solution.STATUS_DRAFT);
        }
        solutionMapper.insert(solution);
    }

    public void updateSolution(Solution solution) {
        solutionMapper.update(solution);
    }

    public void publishSolution(Long id) {
        solutionMapper.updateStatus(id, Solution.STATUS_PUBLISHED);
    }

    public void offlineSolution(Long id) {
        solutionMapper.updateStatus(id, Solution.STATUS_OFFLINE);
    }

    public void useSolution(Long id) {
        solutionMapper.incrementUseCount(id);
    }

    public void likeSolution(Long id) {
        solutionMapper.incrementLikeCount(id);
    }

    public void deleteSolution(Long id) {
        solutionMapper.deleteById(id);
    }
}
