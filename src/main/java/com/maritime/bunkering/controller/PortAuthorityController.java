package com.maritime.bunkering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.maritime.bunkering.common.Result;
import com.maritime.bunkering.dto.SignCheckResultVO;
import com.maritime.bunkering.entity.OilBatch;
import com.maritime.bunkering.entity.SignReceipt;
import com.maritime.bunkering.rule.SulfurContentValidator;
import com.maritime.bunkering.service.OilBatchService;
import com.maritime.bunkering.service.SignReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bunkering/authority")
public class PortAuthorityController {

    @Autowired
    private OilBatchService oilBatchService;

    @Autowired
    private SignReceiptService signReceiptService;

    @GetMapping("/oil-batch/{id}")
    public Result<OilBatch> getOilBatchById(@PathVariable String id) {
        OilBatch batch = oilBatchService.getById(id);
        return Result.success(batch);
    }

    @GetMapping("/oil-batch/batchNo/{batchNo}")
    public Result<OilBatch> getOilBatchByNo(@PathVariable String batchNo) {
        OilBatch batch = oilBatchService.getByBatchNo(batchNo);
        if (batch == null) {
            return Result.error("油品批次不存在: " + batchNo);
        }
        return Result.success(batch);
    }

    @GetMapping("/oil-batch/list")
    public Result<IPage<OilBatch>> queryOilBatchList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String oilType,
            @RequestParam(required = false) Integer batchStatus) {
        IPage<OilBatch> page = oilBatchService.queryPage(pageNum, pageSize, oilType, batchStatus);
        return Result.success(page);
    }

    @GetMapping("/oil-batch/non-compliant")
    public Result<List<OilBatch>> getNonCompliantBatches() {
        List<OilBatch> batches = oilBatchService.queryNonCompliantBatches();
        return Result.success(batches);
    }

    @GetMapping("/oil-batch/check-sulfur/{batchId}")
    public Result<SulfurContentValidator.ValidationResult> checkSulfurContent(@PathVariable String batchId) {
        SulfurContentValidator.ValidationResult result = oilBatchService.checkSulfurContent(batchId);
        return Result.success(result);
    }

    @GetMapping("/sign/check/{applyId}")
    public Result<SignCheckResultVO> getSignCheckResult(@PathVariable String applyId) {
        SignCheckResultVO result = signReceiptService.getSignCheckResult(applyId);
        return Result.success(result);
    }

    @GetMapping("/sign/{id}")
    public Result<SignReceipt> getSignReceiptById(@PathVariable String id) {
        SignReceipt receipt = signReceiptService.getById(id);
        return Result.success(receipt);
    }

    @GetMapping("/sign/list")
    public Result<IPage<SignReceipt>> querySignReceiptList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String applyId,
            @RequestParam(required = false) Integer receiptStatus) {
        IPage<SignReceipt> page = signReceiptService.queryPage(pageNum, pageSize, applyId, receiptStatus);
        return Result.success(page);
    }

    @GetMapping("/sign/history/{applyId}")
    public Result<List<SignReceipt>> getSignHistory(@PathVariable String applyId) {
        List<SignReceipt> history = signReceiptService.getByApplyId(applyId);
        return Result.success(history);
    }
}
