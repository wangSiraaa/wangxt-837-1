package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.dto.BunkeringApplySubmitDTO;
import com.maritime.bunkering.dto.CertReviewDTO;
import com.maritime.bunkering.entity.BunkeringApply;
import com.maritime.bunkering.enums.ApplyStatusEnum;
import com.maritime.bunkering.enums.CertReviewStatusEnum;
import com.maritime.bunkering.mapper.BunkeringApplyMapper;
import com.maritime.bunkering.rule.CertExpireValidator;
import com.maritime.bunkering.rule.SulfurContentValidator;
import com.maritime.bunkering.rule.WorkWindowConflictValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class BunkeringApplyService {

    @Autowired
    private BunkeringApplyMapper bunkeringApplyMapper;

    @Autowired
    private CertExpireValidator certExpireValidator;

    @Autowired
    private SulfurContentValidator sulfurContentValidator;

    @Autowired
    private WorkWindowConflictValidator workWindowConflictValidator;

    @Autowired
    private OilBatchService oilBatchService;

    @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public BunkeringApply submitApply(BunkeringApplySubmitDTO dto) {
        String applyId = "APPLY" + System.currentTimeMillis();
        String applyNo = "BA" + System.currentTimeMillis();

        CertExpireValidator.ValidationResult certResult = certExpireValidator.validate(dto.getShipCode(), dto.getPlanStartTime());

        SulfurContentValidator.ValidationResult sulfurResult = sulfurContentValidator.validate(dto.getOilBatchId());

        WorkWindowConflictValidator.ValidationResult windowResult = workWindowConflictValidator.validate(
                dto.getBerthCode(), dto.getPlanStartTime(), dto.getPlanEndTime(), null
        );

        Integer applyStatus = ApplyStatusEnum.SUBMITTED.getCode();
        Integer sulfurCheckResult = 0;
        Integer windowConflictFlag = 0;
        String rejectReason = null;

        if (!certResult.isValid()) {
            applyStatus = ApplyStatusEnum.REJECTED.getCode();
            rejectReason = certResult.getMessage();
        }

        if (!sulfurResult.isCompliant()) {
            applyStatus = ApplyStatusEnum.REJECTED.getCode();
            sulfurCheckResult = 2;
            if (rejectReason == null) {
                rejectReason = sulfurResult.getMessage();
            } else {
                rejectReason = rejectReason + "; " + sulfurResult.getMessage();
            }
        } else {
            sulfurCheckResult = 1;
        }

        if (windowResult.isConflict()) {
            applyStatus = ApplyStatusEnum.REJECTED.getCode();
            windowConflictFlag = 1;
            if (rejectReason == null) {
                rejectReason = windowResult.getMessage();
            } else {
                rejectReason = rejectReason + "; " + windowResult.getMessage();
            }
        }

        BunkeringApply apply = new BunkeringApply();
        apply.setId(applyId);
        apply.setApplyNo(applyNo);
        apply.setShipCode(dto.getShipCode());
        apply.setShipName(dto.getShipName());
        apply.setAgentCode(dto.getAgentCode());
        apply.setAgentName(dto.getAgentName());
        apply.setSupplierCode(dto.getSupplierCode());
        apply.setSupplierName(dto.getSupplierName());
        apply.setOilBatchId(dto.getOilBatchId());
        apply.setOilType(dto.getOilType());
        apply.setOilQuantity(dto.getOilQuantity());
        apply.setBerthCode(dto.getBerthCode());
        apply.setWorkWindowId(windowResult.getWindowId());
        apply.setPlanStartTime(dto.getPlanStartTime());
        apply.setPlanEndTime(dto.getPlanEndTime());
        apply.setCertReviewStatus(CertReviewStatusEnum.PENDING.getCode());
        apply.setApplyStatus(applyStatus);
        apply.setRejectReason(rejectReason);
        apply.setSulfurCheckResult(sulfurCheckResult);
        apply.setWindowConflictFlag(windowConflictFlag);
        apply.setCreateBy(dto.getCreateBy());
        apply.setUpdateBy(dto.getCreateBy());

        bunkeringApplyMapper.insert(apply);

        if (ApplyStatusEnum.REJECTED.getCode().equals(applyStatus)) {
            throw new BusinessException(rejectReason);
        }

        return apply;
    }

    @Transactional(rollbackFor = Exception.class)
    public BunkeringApply reviewCert(CertReviewDTO dto) {
        BunkeringApply apply = bunkeringApplyMapper.selectById(dto.getApplyId());
        if (apply == null) {
            throw new BusinessException("加注申请不存在: " + dto.getApplyId());
        }

        if (!ApplyStatusEnum.SUBMITTED.getCode().equals(apply.getApplyStatus())
                && !ApplyStatusEnum.CERT_PASSED.getCode().equals(apply.getApplyStatus())) {
            throw new BusinessException(
                    "当前申请状态【" + ApplyStatusEnum.getDescByCode(apply.getApplyStatus()) +
                            "】不允许进行证书复核"
            );
        }

        if (CertReviewStatusEnum.REJECTED.getCode().equals(dto.getReviewStatus())) {
            apply.setCertReviewStatus(CertReviewStatusEnum.REJECTED.getCode());
            apply.setCertReviewComment(dto.getReviewComment());
            apply.setCertReviewTime(new Date());
            apply.setCertReviewer(dto.getReviewer());
            apply.setApplyStatus(ApplyStatusEnum.REJECTED.getCode());
            apply.setRejectReason("证书复核不通过: " + dto.getReviewComment());
            apply.setUpdateBy(dto.getReviewer());
            bunkeringApplyMapper.updateById(apply);
            return apply;
        }

        apply.setCertReviewStatus(CertReviewStatusEnum.PASSED.getCode());
        apply.setCertReviewComment(dto.getReviewComment());
        apply.setCertReviewTime(new Date());
        apply.setCertReviewer(dto.getReviewer());
        apply.setApplyStatus(ApplyStatusEnum.CERT_PASSED.getCode());
        apply.setUpdateBy(dto.getReviewer());

        SulfurContentValidator.ValidationResult sulfurResult =
                sulfurContentValidator.validate(apply.getOilBatchId());

        if (!sulfurResult.isCompliant()) {
            apply.setSulfurCheckResult(2);
            apply.setApplyStatus(ApplyStatusEnum.REJECTED.getCode());
            apply.setRejectReason(sulfurResult.getMessage());
            bunkeringApplyMapper.updateById(apply);
            throw new BusinessException(sulfurResult.getMessage());
        }

        apply.setSulfurCheckResult(1);
        apply.setApplyStatus(ApplyStatusEnum.OIL_CHECK_PASSED.getCode());

        bunkeringApplyMapper.updateById(apply);
        return apply;
    }

    @Transactional(rollbackFor = Exception.class)
    public BunkeringApply confirmApply(String applyId, String operator) {
        BunkeringApply apply = bunkeringApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException("加注申请不存在: " + applyId);
        }

        Integer currentStatus = apply.getApplyStatus();
        if (!ApplyStatusEnum.OIL_CHECK_PASSED.getCode().equals(currentStatus)
                && !ApplyStatusEnum.PENDING_CONFIRM.getCode().equals(currentStatus)) {
            throw new BusinessException(
                    "当前申请状态【" + ApplyStatusEnum.getDescByCode(currentStatus) +
                            "】不允许进行确认操作"
            );
        }

        if (apply.getSulfurCheckResult() != null && apply.getSulfurCheckResult() == 2) {
            apply.setApplyStatus(ApplyStatusEnum.REJECTED.getCode());
            apply.setWindowConflictFlag(1);
            apply.setRejectReason("油品硫含量超标，拒绝确认");
            apply.setUpdateBy(operator);
            bunkeringApplyMapper.updateById(apply);
            throw new BusinessException("油品硫含量超标，拒绝确认，申请状态已变更为已拒绝");
        }

        WorkWindowConflictValidator.ValidationResult windowResult = workWindowConflictValidator.validate(
                apply.getBerthCode(), apply.getPlanStartTime(), apply.getPlanEndTime(), applyId
        );

        if (windowResult.isConflict()) {
            apply.setApplyStatus(ApplyStatusEnum.REJECTED.getCode());
            apply.setWindowConflictFlag(1);
            apply.setRejectReason(windowResult.getMessage());
            apply.setUpdateBy(operator);
            bunkeringApplyMapper.updateById(apply);
            throw new BusinessException(
                    windowResult.getMessage() + "，拒绝确认，申请状态已变更为已拒绝"
            );
        }

        apply.setApplyStatus(ApplyStatusEnum.CONFIRMED.getCode());
        apply.setConfirmTime(new Date());
        apply.setWindowConflictFlag(0);
        apply.setUpdateBy(operator);
        bunkeringApplyMapper.updateById(apply);

        return apply;
    }

    public BunkeringApply getById(String id) {
        return bunkeringApplyMapper.selectById(id);
    }

    public IPage<BunkeringApply> queryPage(int pageNum, int pageSize, String shipCode, Integer applyStatus) {
        Page<BunkeringApply> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BunkeringApply> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (shipCode != null && !shipCode.isEmpty()) {
            wrapper.eq("ship_code", shipCode);
        }
        if (applyStatus != null) {
            wrapper.eq("apply_status", applyStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return bunkeringApplyMapper.selectPage(page, wrapper);
    }

    public IPage<BunkeringApply> queryBySupplier(int pageNum, int pageSize, String supplierCode, Integer certReviewStatus) {
        Page<BunkeringApply> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BunkeringApply> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (supplierCode != null && !supplierCode.isEmpty()) {
            wrapper.eq("supplier_code", supplierCode);
        }
        if (certReviewStatus != null) {
            wrapper.eq("cert_review_status", certReviewStatus);
        }
        wrapper.in("apply_status", ApplyStatusEnum.SUBMITTED.getCode(),
                ApplyStatusEnum.CERT_PASSED.getCode(),
                ApplyStatusEnum.OIL_CHECK_PASSED.getCode());
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return bunkeringApplyMapper.selectPage(page, wrapper);
    }
}
