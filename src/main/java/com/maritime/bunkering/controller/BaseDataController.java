package com.maritime.bunkering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.maritime.bunkering.common.Result;
import com.maritime.bunkering.entity.*;
import com.maritime.bunkering.rule.WorkWindowConflictValidator;
import com.maritime.bunkering.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bunkering/base")
public class BaseDataController {

    @Autowired
    private ShipCertService shipCertService;

    @Autowired
    private OilBatchService oilBatchService;

    @Autowired
    private WorkWindowService workWindowService;

    @Autowired
    private BerthPlanService berthPlanService;

    @GetMapping("/ship-cert/{id}")
    public Result<ShipCert> getShipCertById(@PathVariable String id) {
        return Result.success(shipCertService.getById(id));
    }

    @GetMapping("/ship-cert/list")
    public Result<IPage<ShipCert>> queryShipCertList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String shipCode,
            @RequestParam(required = false) Integer certStatus) {
        return Result.success(shipCertService.queryPage(pageNum, pageSize, shipCode, certStatus));
    }

    @GetMapping("/ship-cert/ship/{shipCode}")
    public Result<List<ShipCert>> getShipCertsByShipCode(@PathVariable String shipCode) {
        return Result.success(shipCertService.getByShipCode(shipCode));
    }

    @PostMapping("/ship-cert")
    public Result<ShipCert> createShipCert(@RequestBody ShipCert cert) {
        return Result.success("船舶证书创建成功", shipCertService.create(cert));
    }

    @PutMapping("/ship-cert")
    public Result<ShipCert> updateShipCert(@RequestBody ShipCert cert) {
        return Result.success("船舶证书更新成功", shipCertService.update(cert));
    }

    @GetMapping("/work-window/{id}")
    public Result<WorkWindow> getWorkWindowById(@PathVariable String id) {
        return Result.success(workWindowService.getById(id));
    }

    @GetMapping("/work-window/list")
    public Result<IPage<WorkWindow>> queryWorkWindowList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String berthCode,
            @RequestParam(required = false) Integer windowStatus) {
        return Result.success(workWindowService.queryPage(pageNum, pageSize, berthCode, windowStatus));
    }

    @GetMapping("/work-window/available")
    public Result<List<WorkWindow>> getAvailableWindows(
            @RequestParam String berthCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date windowDate) {
        return Result.success(workWindowService.getAvailableWindows(berthCode, windowDate));
    }

    @GetMapping("/work-window/check-conflict")
    public Result<WorkWindowConflictValidator.ValidationResult> checkWindowConflict(
            @RequestParam String berthCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date planStartTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date planEndTime) {
        return Result.success(workWindowService.checkConflict(berthCode, planStartTime, planEndTime));
    }

    @PostMapping("/work-window")
    public Result<WorkWindow> createWorkWindow(@RequestBody WorkWindow window) {
        return Result.success("作业窗口创建成功", workWindowService.create(window));
    }

    @PutMapping("/work-window")
    public Result<WorkWindow> updateWorkWindow(@RequestBody WorkWindow window) {
        return Result.success("作业窗口更新成功", workWindowService.update(window));
    }

    @GetMapping("/berth-plan/{id}")
    public Result<BerthPlan> getBerthPlanById(@PathVariable String id) {
        return Result.success(berthPlanService.getById(id));
    }

    @GetMapping("/berth-plan/list")
    public Result<IPage<BerthPlan>> queryBerthPlanList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String berthCode,
            @RequestParam(required = false) String shipCode,
            @RequestParam(required = false) Integer planStatus) {
        return Result.success(berthPlanService.queryPage(pageNum, pageSize, berthCode, shipCode, planStatus));
    }

    @PostMapping("/berth-plan")
    public Result<BerthPlan> createBerthPlan(@RequestBody BerthPlan plan) {
        return Result.success("靠泊计划创建成功", berthPlanService.create(plan));
    }

    @PutMapping("/berth-plan")
    public Result<BerthPlan> updateBerthPlan(@RequestBody BerthPlan plan) {
        return Result.success("靠泊计划更新成功", berthPlanService.update(plan));
    }

    @GetMapping("/oil-batch/{id}")
    public Result<OilBatch> getOilBatchById(@PathVariable String id) {
        return Result.success(oilBatchService.getById(id));
    }

    @GetMapping("/oil-batch/list")
    public Result<IPage<OilBatch>> queryOilBatchList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String oilType,
            @RequestParam(required = false) Integer batchStatus) {
        return Result.success(oilBatchService.queryPage(pageNum, pageSize, oilType, batchStatus));
    }

    @PostMapping("/oil-batch")
    public Result<OilBatch> createOilBatch(@RequestBody OilBatch batch) {
        return Result.success("油品批次创建成功", oilBatchService.create(batch));
    }

    @PutMapping("/oil-batch")
    public Result<OilBatch> updateOilBatch(@RequestBody OilBatch batch) {
        return Result.success("油品批次更新成功", oilBatchService.update(batch));
    }
}
