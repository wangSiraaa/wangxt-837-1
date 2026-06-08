package com.maritime.bunkering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.maritime.bunkering.common.Result;
import com.maritime.bunkering.dto.CertReviewDTO;
import com.maritime.bunkering.dto.SignReceiptSubmitDTO;
import com.maritime.bunkering.entity.BunkeringApply;
import com.maritime.bunkering.entity.ShipCert;
import com.maritime.bunkering.entity.SignReceipt;
import com.maritime.bunkering.service.BunkeringApplyService;
import com.maritime.bunkering.service.ShipCertService;
import com.maritime.bunkering.service.SignReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bunkering/supplier")
@Validated
public class OilSupplierController {

    @Autowired
    private BunkeringApplyService bunkeringApplyService;

    @Autowired
    private ShipCertService shipCertService;

    @Autowired
    private SignReceiptService signReceiptService;

    @GetMapping("/apply/pending")
    public Result<IPage<BunkeringApply>> queryPendingApply(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) Integer certReviewStatus) {
        IPage<BunkeringApply> page = bunkeringApplyService.queryBySupplier(
                pageNum, pageSize, supplierCode, certReviewStatus);
        return Result.success(page);
    }

    @PostMapping("/cert/review")
    public Result<BunkeringApply> reviewCert(@Valid @RequestBody CertReviewDTO dto) {
        BunkeringApply apply = bunkeringApplyService.reviewCert(dto);
        return Result.success("证书复核完成", apply);
    }

    @GetMapping("/ship/certs/{shipCode}")
    public Result<List<ShipCert>> getShipCerts(@PathVariable String shipCode) {
        List<ShipCert> certs = shipCertService.getByShipCode(shipCode);
        return Result.success(certs);
    }

    @GetMapping("/apply/{id}")
    public Result<BunkeringApply> getApplyById(@PathVariable String id) {
        BunkeringApply apply = bunkeringApplyService.getById(id);
        return Result.success(apply);
    }

    @PostMapping("/sign/submit")
    public Result<SignReceipt> submitSignReceipt(@Valid @RequestBody SignReceiptSubmitDTO dto) {
        SignReceipt receipt = signReceiptService.submitSignReceipt(dto);
        return Result.success("签收单提交成功", receipt);
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
}
