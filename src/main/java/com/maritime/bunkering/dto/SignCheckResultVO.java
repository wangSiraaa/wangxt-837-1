package com.maritime.bunkering.dto;

import com.maritime.bunkering.entity.OilBatch;
import com.maritime.bunkering.entity.ShipCert;
import com.maritime.bunkering.entity.WorkWindow;
import com.maritime.bunkering.rule.SulfurContentValidator;
import com.maritime.bunkering.rule.WorkWindowConflictValidator;

import java.math.BigDecimal;
import java.util.List;

public class SignCheckResultVO {

    private String applyId;
    private String applyNo;
    private String shipCode;
    private String shipName;
    private String supplierCode;
    private String supplierName;
    private BigDecimal planQuantity;
    private BigDecimal actualQuantity;
    private Integer applyStatus;
    private String applyStatusDesc;

    private List<ShipCert> shipCerts;
    private Integer certCheckResult;
    private String certCheckMsg;

    private OilBatch oilBatch;
    private SulfurContentValidator.ValidationResult sulfurCheckResult;

    private WorkWindow workWindow;
    private WorkWindowConflictValidator.ValidationResult windowCheckResult;

    private String signer;
    private java.util.Date signTime;
    private String remark;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getShipCode() {
        return shipCode;
    }

    public void setShipCode(String shipCode) {
        this.shipCode = shipCode;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getPlanQuantity() {
        return planQuantity;
    }

    public void setPlanQuantity(BigDecimal planQuantity) {
        this.planQuantity = planQuantity;
    }

    public BigDecimal getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(BigDecimal actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatusDesc() {
        return applyStatusDesc;
    }

    public void setApplyStatusDesc(String applyStatusDesc) {
        this.applyStatusDesc = applyStatusDesc;
    }

    public List<ShipCert> getShipCerts() {
        return shipCerts;
    }

    public void setShipCerts(List<ShipCert> shipCerts) {
        this.shipCerts = shipCerts;
    }

    public Integer getCertCheckResult() {
        return certCheckResult;
    }

    public void setCertCheckResult(Integer certCheckResult) {
        this.certCheckResult = certCheckResult;
    }

    public String getCertCheckMsg() {
        return certCheckMsg;
    }

    public void setCertCheckMsg(String certCheckMsg) {
        this.certCheckMsg = certCheckMsg;
    }

    public OilBatch getOilBatch() {
        return oilBatch;
    }

    public void setOilBatch(OilBatch oilBatch) {
        this.oilBatch = oilBatch;
    }

    public SulfurContentValidator.ValidationResult getSulfurCheckResult() {
        return sulfurCheckResult;
    }

    public void setSulfurCheckResult(SulfurContentValidator.ValidationResult sulfurCheckResult) {
        this.sulfurCheckResult = sulfurCheckResult;
    }

    public WorkWindow getWorkWindow() {
        return workWindow;
    }

    public void setWorkWindow(WorkWindow workWindow) {
        this.workWindow = workWindow;
    }

    public WorkWindowConflictValidator.ValidationResult getWindowCheckResult() {
        return windowCheckResult;
    }

    public void setWindowCheckResult(WorkWindowConflictValidator.ValidationResult windowCheckResult) {
        this.windowCheckResult = windowCheckResult;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public java.util.Date getSignTime() {
        return signTime;
    }

    public void setSignTime(java.util.Date signTime) {
        this.signTime = signTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
