package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.entity.WorkWindow;
import com.maritime.bunkering.mapper.WorkWindowMapper;
import com.maritime.bunkering.rule.WorkWindowConflictValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WorkWindowService {

    @Autowired
    private WorkWindowMapper workWindowMapper;

    @Autowired
    private WorkWindowConflictValidator conflictValidator;

    public WorkWindow getById(String id) {
        WorkWindow window = workWindowMapper.selectById(id);
        if (window == null) {
            throw new BusinessException("作业窗口不存在: " + id);
        }
        return window;
    }

    public List<WorkWindow> getAvailableWindows(String berthCode, Date windowDate) {
        return workWindowMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkWindow>()
                        .eq("berth_code", berthCode)
                        .eq("window_date", windowDate)
                        .eq("window_status", 1)
                        .eq("deleted", 0)
                        .orderByAsc("start_time")
        );
    }

    public IPage<WorkWindow> queryPage(int pageNum, int pageSize, String berthCode, Integer windowStatus) {
        Page<WorkWindow> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkWindow> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (berthCode != null && !berthCode.isEmpty()) {
            wrapper.eq("berth_code", berthCode);
        }
        if (windowStatus != null) {
            wrapper.eq("window_status", windowStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("window_date", "start_time");
        return workWindowMapper.selectPage(page, wrapper);
    }

    public WorkWindowConflictValidator.ValidationResult checkConflict(
            String berthCode, Date planStartTime, Date planEndTime) {
        return conflictValidator.validate(berthCode, planStartTime, planEndTime, null);
    }

    public WorkWindow create(WorkWindow window) {
        if (window.getId() == null || window.getId().isEmpty()) {
            window.setId("WIN" + System.currentTimeMillis());
        }
        if (window.getWindowStatus() == null) {
            window.setWindowStatus(1);
        }
        if (window.getMaxVessels() == null) {
            window.setMaxVessels(1);
        }
        workWindowMapper.insert(window);
        return window;
    }

    public WorkWindow update(WorkWindow window) {
        WorkWindow existing = workWindowMapper.selectById(window.getId());
        if (existing == null) {
            throw new BusinessException("作业窗口不存在: " + window.getId());
        }
        workWindowMapper.updateById(window);
        return workWindowMapper.selectById(window.getId());
    }

    public void updateStatus(String id, Integer status) {
        WorkWindow window = workWindowMapper.selectById(id);
        if (window == null) {
            throw new BusinessException("作业窗口不存在: " + id);
        }
        window.setWindowStatus(status);
        workWindowMapper.updateById(window);
    }
}
