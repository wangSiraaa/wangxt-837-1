package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.dto.SignCheckResultVO;
import com.maritime.bunkering.dto.SignReceiptSubmitDTO;
import com.maritime.bunkering.entity.*;
import com.maritime.bunkering.enums.ApplyStatusEnum;
import com.maritime.bunkering.mapper.BunkeringApplyMapper;
import com.maritime.bunkering.mapper.SignReceiptMapper;
import com.maritime.bunkering.mapper.WorkWindowMapper;
import com.maritime.bunkering.rule.CertExpireValidator;
import com.maritime.bunkering.rule.SulfurContentValidator;
import com.maritime.bunkering.rule.WorkWindowConflictValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SignReceiptService {

    @Autowired
    private SignReceiptMapper signReceiptMapper;

    @Autowired
    private BunkeringApplyMapper bunkeringApplyMapper;

    @Autowired
    private WorkWindowMapper workWindowMapper;

    @Autowired
    private CertExpireValidator certExpireValidator;

    @Autowired
    private SulfurContentValidator sulfurContentValidator;

    @Autowired
    private WorkWindowConflictValidator workWindowConflictValidator;

    @Autowired
    private ShipCertService shipCertService;

    @Autowired
    private OilBatchService oilBatchService;

    @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public SignReceipt submitSignReceipt(SignReceiptSubmitDTO dto) {
        BunkeringApply apply = bunkeringApplyMapper.selectById(dto.getApplyId());
        if (apply == null) {
            throw new BusinessException("加注申请不存在: " + dto.getApplyId());
        }

        Integer currentStatus = apply.getApplyStatus();
        if (!ApplyStatusEnum.CONFIRMED.getCode().equals(currentStatus)
                && !ApplyStatusEnum.PENDING_SIGN.getCode().equals(currentStatus)) {
            throw new BusinessException(
                    "当前申请状态【" + ApplyStatusEnum.getDescByCode(currentStatus) +
                            "】不允许提交签收单"
            );
        }

        Integer beforeStatus = apply.getApplyStatus();
        Integer afterStatus;
        Integer receiptStatus;
        String rejectReason = null;

        CertExpireValidator.ValidationResult certResult = certExpireValidator.validate(
                apply.getShipCode(), dto.getSignTime());

        SulfurContentValidator.ValidationResult sulfurResult = sulfurContentValidator.validate(
                apply.getOilBatchId());

        WorkWindowConflictValidator.ValidationResult windowResult = workWindowConflictValidator.validate(
                apply.getBerthCode(), apply.getPlanStartTime(), apply.getPlanEndTime(), apply.getId());

        Integer certCheckResult = certResult.isValid() ? 1 : 2;
        Integer oilCheckResult = sulfurResult.isCompliant() ? 1 : 2;
        Integer windowCheckResult = windowResult.isConflict() ? 2 : 1;

        if (!sulfurResult.isCompliant()) {
            afterStatus = ApplyStatusEnum.SIGN_REJECTED.getCode();
            receiptStatus = 2;
            rejectReason = "油品硫含量超标，签收不通过。" + sulfurResult.getMessage();
        } else if (windowResult.isConflict()) {
            afterStatus = ApplyStatusEnum.SIGN_REJECTED.getCode();
            receiptStatus = 2;
            rejectReason = "靠泊计划冲突，签收不通过。" + windowResult.getMessage();
        } else if (!certResult.isValid()) {
            afterStatus = ApplyStatusEnum.SIGN_REJECTED.getCode();
            receiptStatus = 2;
            rejectReason = "船舶证书校验不通过，签收不通过。" + certResult.getMessage();
        } else {
            afterStatus = ApplyStatusEnum.SIGN_RECEIVED.getCode();
            receiptStatus = 1;
        }

        String receiptId = "SIGN" + System.currentTimeMillis();
        String receiptNo = "SR" + System.currentTimeMillis();

        SignReceipt receipt = new SignReceipt();
        receipt.setId(receiptId);
        receipt.setReceiptNo(receiptNo);
        receipt.setApplyId(apply.getId());
        receipt.setApplyNo(apply.getApplyNo());
        receipt.setShipCode(apply.getShipCode());
        receipt.setShipName(apply.getShipName());
        receipt.setSupplierCode(apply.getSupplierCode());
        receipt.setSupplierName(apply.getSupplierName());
        receipt.setActualQuantity(dto.getActualQuantity());
        receipt.setSignTime(dto.getSignTime());
        receipt.setSigner(dto.getSigner());
        receipt.setBeforeStatus(beforeStatus);
        receipt.setAfterStatus(afterStatus);
        receipt.setCertCheckResult(certCheckResult);
        receipt.setCertCheckMsg(certResult.getMessage());
        receipt.setOilCheckResult(oilCheckResult);
        receipt.setOilCheckMsg(sulfurResult.getMessage());
        receipt.setWindowCheckResult(windowCheckResult);
        receipt.setWindowCheckMsg(windowResult.getMessage());
        receipt.setReceiptStatus(receiptStatus);
        receipt.setRejectReason(rejectReason);
        receipt.setRemark(dto.getRemark());
        receipt.setCreateBy(dto.getOperator());
        receipt.setUpdateBy(dto.getOperator());

        signReceiptMapper.insert(receipt);

        apply.setApplyStatus(afterStatus);
        apply.setUpdateBy(dto.getOperator());
        if (rejectReason != null) {
            apply.setRejectReason(rejectReason);
        }
        bunkeringApplyMapper.updateById(apply);

        if (ApplyStatusEnum.SIGN_REJECTED.getCode().equals(afterStatus)) {
            throw new BusinessException(rejectReason + "，签收单已记录但未通过审核");
        }

        return receipt;
    }

    public SignCheckResultVO getSignCheckResult(String applyId) {
        BunkeringApply apply = bunkeringApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException("加注申请不存在: " + applyId);
        }

        SignCheckResultVO vo = new SignCheckResultVO();
        vo.setApplyId(apply.getId());
        vo.setApplyNo(apply.getApplyNo());
        vo.setShipCode(apply.getShipCode());
        vo.setShipName(apply.getShipName());
        vo.setSupplierCode(apply.getSupplierCode());
        vo.setSupplierName(apply.getSupplierName());
        vo.setPlanQuantity(apply.getOilQuantity());
        vo.setApplyStatus(apply.getApplyStatus());
        vo.setApplyStatusDesc(ApplyStatusEnum.getDescByCode(apply.getApplyStatus()));

        List<ShipCert> certs = shipCertService.getByShipCode(apply.getShipCode());
        vo.setShipCerts(certs);

        CertExpireValidator.ValidationResult certResult = certExpireValidator.validate(
                apply.getShipCode(), new Date());
        vo.setCertCheckResult(certResult.isValid() ? 1 : 2);
        vo.setCertCheckMsg(certResult.getMessage());

        OilBatch oilBatch = oilBatchService.getById(apply.getOilBatchId());
        vo.setOilBatch(oilBatch);

        SulfurContentValidator.ValidationResult sulfurResult = sulfurContentValidator.validate(
                apply.getOilBatchId());
        vo.setSulfurCheckResult(sulfurResult);

        if (apply.getWorkWindowId() != null) {
            WorkWindow window = workWindowMapper.selectById(apply.getWorkWindowId());
            vo.setWorkWindow(window);
        }

        WorkWindowConflictValidator.ValidationResult windowResult = workWindowConflictValidator.validate(
                apply.getBerthCode(), apply.getPlanStartTime(), apply.getPlanEndTime(), apply.getId());
        vo.setWindowCheckResult(windowResult);

        List<SignReceipt> receipts = signReceiptMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SignReceipt>()
                        .eq("apply_id", applyId)
                        .eq("deleted", 0)
                        .orderByDesc("create_time")
        );

        if (receipts != null && !receipts.isEmpty()) {
            SignReceipt latest = receipts.get(0);
            vo.setActualQuantity(latest.getActualQuantity());
            vo.setSigner(latest.getSigner());
            vo.setSignTime(latest.getSignTime());
            vo.setRemark(latest.getRemark());
        }

        return vo;
    }

    public SignReceipt getById(String id) {
        return signReceiptMapper.selectById(id);
    }

    public IPage<SignReceipt> queryPage(int pageNum, int pageSize, String applyId, Integer receiptStatus) {
        Page<SignReceipt> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SignReceipt> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (applyId != null && !applyId.isEmpty()) {
            wrapper.eq("apply_id", applyId);
        }
        if (receiptStatus != null) {
            wrapper.eq("receipt_status", receiptStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return signReceiptMapper.selectPage(page, wrapper);
    }

    public List<SignReceipt> getByApplyId(String applyId) {
        return signReceiptMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SignReceipt>()
                        .eq("apply_id", applyId)
                        .eq("deleted", 0)
                        .orderByDesc("create_time")
        );
    }
}
