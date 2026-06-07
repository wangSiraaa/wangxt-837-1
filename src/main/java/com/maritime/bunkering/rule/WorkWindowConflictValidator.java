package com.maritime.bunkering.rule;

import com.maritime.bunkering.entity.BerthPlan;
import com.maritime.bunkering.entity.WorkWindow;
import com.maritime.bunkering.mapper.BerthPlanMapper;
import com.maritime.bunkering.mapper.WorkWindowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class WorkWindowConflictValidator {

    @Autowired
    private WorkWindowMapper workWindowMapper;

    @Autowired
    private BerthPlanMapper berthPlanMapper;

    public ValidationResult validate(String berthCode, Date planStartTime, Date planEndTime, String excludeApplyId) {
        ValidationResult result = new ValidationResult();
        result.setConflict(false);

        LocalDateTime planStart = toLocalDateTime(planStartTime);
        LocalDateTime planEnd = toLocalDateTime(planEndTime);

        List<WorkWindow> windows = workWindowMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkWindow>()
                        .eq("berth_code", berthCode)
                        .eq("window_status", 1)
                        .eq("deleted", 0)
        );

        if (windows == null || windows.isEmpty()) {
            result.setConflict(true);
            result.setMessage("该泊位在计划时间段内没有可用作业窗口");
            return result;
        }

        boolean inWindow = false;
        for (WorkWindow window : windows) {
            LocalDateTime windowStart = LocalDateTime.of(
                    toLocalDateTime(window.getWindowDate()).toLocalDate(),
                    toLocalTime(window.getStartTime())
            );
            LocalDateTime windowEnd = LocalDateTime.of(
                    toLocalDateTime(window.getWindowDate()).toLocalDate(),
                    toLocalTime(window.getEndTime())
            );

            if (!planStart.isBefore(windowStart) && !planEnd.isAfter(windowEnd)) {
                inWindow = true;
                result.setWindowId(window.getId());
                result.setWindowCode(window.getWindowCode());
                result.setWindowName(window.getWindowName());
                break;
            }
        }

        if (!inWindow) {
            result.setConflict(true);
            result.setMessage("计划作业时间不在可用作业窗口范围内，请调整作业时间");
            return result;
        }

        List<BerthPlan> plans = berthPlanMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BerthPlan>()
                        .eq("berth_code", berthCode)
                        .in("plan_status", 1, 2)
                        .eq("deleted", 0)
        );

        if (plans != null && !plans.isEmpty()) {
            for (BerthPlan plan : plans) {
                LocalDateTime existingStart = toLocalDateTime(plan.getPlanStartTime());
                LocalDateTime existingEnd = toLocalDateTime(plan.getPlanEndTime());

                if (isOverlap(planStart, planEnd, existingStart, existingEnd)) {
                    result.setConflict(true);
                    result.setMessage(
                            "作业窗口与靠泊计划冲突。冲突计划: " + plan.getPlanNo() +
                                    "，船舶: " + plan.getShipName() +
                                    "，作业类型: " + plan.getOperationType() +
                                    "，时间: " + plan.getPlanStartTime() + " 至 " + plan.getPlanEndTime()
                    );
                    return result;
                }
            }
        }

        if (!result.isConflict()) {
            result.setMessage("作业窗口校验通过，无冲突");
        }

        return result;
    }

    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1,
                              LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate().atStartOfDay();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private LocalTime toLocalTime(java.sql.Time time) {
        if (time == null) {
            return LocalTime.MIDNIGHT;
        }
        return time.toLocalTime();
    }

    public static class ValidationResult {
        private boolean conflict;
        private String message;
        private String windowId;
        private String windowCode;
        private String windowName;

        public boolean isConflict() { return conflict; }
        public void setConflict(boolean conflict) { this.conflict = conflict; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getWindowId() { return windowId; }
        public void setWindowId(String windowId) { this.windowId = windowId; }
        public String getWindowCode() { return windowCode; }
        public void setWindowCode(String windowCode) { this.windowCode = windowCode; }
        public String getWindowName() { return windowName; }
        public void setWindowName(String windowName) { this.windowName = windowName; }
    }
}
