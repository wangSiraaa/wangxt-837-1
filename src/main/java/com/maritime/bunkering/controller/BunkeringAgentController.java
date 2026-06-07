package com.maritime.bunkering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.maritime.bunkering.common.Result;
import com.maritime.bunkering.dto.BunkeringApplySubmitDTO;
import com.maritime.bunkering.entity.BunkeringApply;
import com.maritime.bunkering.service.BunkeringApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/bunkering/agent")
@Validated
public class BunkeringAgentController {

    @Autowired
    private BunkeringApplyService bunkeringApplyService;

    @PostMapping("/apply/submit")
    public Result<BunkeringApply> submitApply(@Valid @RequestBody BunkeringApplySubmitDTO dto) {
        BunkeringApply apply = bunkeringApplyService.submitApply(dto);
        return Result.success("加注申请提交成功", apply);
    }

    @GetMapping("/apply/{id}")
    public Result<BunkeringApply> getApplyById(@PathVariable String id) {
        BunkeringApply apply = bunkeringApplyService.getById(id);
        return Result.success(apply);
    }

    @GetMapping("/apply/list")
    public Result<IPage<BunkeringApply>> queryApplyList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String shipCode,
            @RequestParam(required = false) Integer applyStatus) {
        IPage<BunkeringApply> page = bunkeringApplyService.queryPage(pageNum, pageSize, shipCode, applyStatus);
        return Result.success(page);
    }

    @PostMapping("/apply/confirm/{id}")
    public Result<BunkeringApply> confirmApply(
            @PathVariable String id,
            @RequestParam String operator) {
        BunkeringApply apply = bunkeringApplyService.confirmApply(id, operator);
        return Result.success("加注申请确认成功", apply);
    }
}
