package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.entity.BerthPlan;
import com.maritime.bunkering.mapper.BerthPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BerthPlanService {

    @Autowired
    private BerthPlanMapper berthPlanMapper;

    public BerthPlan getById(String id) {
        BerthPlan plan = berthPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("靠泊计划不存在: " + id);
        }
        return plan;
    }

    public List<BerthPlan> getByShipCode(String shipCode) {
        return berthPlanMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BerthPlan>()
                        .eq("ship_code", shipCode)
                        .eq("deleted", 0)
                        .orderByDesc("plan_start_time")
        );
    }

    public List<BerthPlan> getByBerthAndDate(String berthCode, Date planDate) {
        return berthPlanMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BerthPlan>()
                        .eq("berth_code", berthCode)
                        .eq("plan_berth_date", planDate)
                        .in("plan_status", 1, 2)
                        .eq("deleted", 0)
                        .orderByAsc("plan_start_time")
        );
    }

    public IPage<BerthPlan> queryPage(int pageNum, int pageSize, String berthCode, String shipCode, Integer planStatus) {
        Page<BerthPlan> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BerthPlan> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (berthCode != null && !berthCode.isEmpty()) {
            wrapper.eq("berth_code", berthCode);
        }
        if (shipCode != null && !shipCode.isEmpty()) {
            wrapper.eq("ship_code", shipCode);
        }
        if (planStatus != null) {
            wrapper.eq("plan_status", planStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("plan_start_time");
        return berthPlanMapper.selectPage(page, wrapper);
    }

    public BerthPlan create(BerthPlan plan) {
        if (plan.getId() == null || plan.getId().isEmpty()) {
            plan.setId("PLAN" + System.currentTimeMillis());
        }
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus(1);
        }
        berthPlanMapper.insert(plan);
        return plan;
    }

    public BerthPlan update(BerthPlan plan) {
        BerthPlan existing = berthPlanMapper.selectById(plan.getId());
        if (existing == null) {
            throw new BusinessException("靠泊计划不存在: " + plan.getId());
        }
        berthPlanMapper.updateById(plan);
        return berthPlanMapper.selectById(plan.getId());
    }

    public void updateStatus(String id, Integer status) {
        BerthPlan plan = berthPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("靠泊计划不存在: " + id);
        }
        plan.setPlanStatus(status);
        berthPlanMapper.updateById(plan);
    }
}
