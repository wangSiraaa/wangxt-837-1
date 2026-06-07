package com.maritime.bunkering.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("bunkering_apply")
public class BunkeringApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String applyNo;

    private String shipCode;

    private String shipName;

    private String agentCode;

    private String agentName;

    private String supplierCode;

    private String supplierName;

    private String oilBatchId;

    private String oilType;

    private BigDecimal oilQuantity;

    private String berthCode;

    private String workWindowId;

    private Date planStartTime;

    private Date planEndTime;

    private Integer certReviewStatus;

    private String certReviewComment;

    private Date certReviewTime;

    private String certReviewer;

    private Integer applyStatus;

    private String rejectReason;

    private Date confirmTime;

    private Integer sulfurCheckResult;

    private Integer windowConflictFlag;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String createBy;

    private String updateBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
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

    public String getOilBatchId() {
        return oilBatchId;
    }

    public void setOilBatchId(String oilBatchId) {
        this.oilBatchId = oilBatchId;
    }

    public String getOilType() {
        return oilType;
    }

    public void setOilType(String oilType) {
        this.oilType = oilType;
    }

    public BigDecimal getOilQuantity() {
        return oilQuantity;
    }

    public void setOilQuantity(BigDecimal oilQuantity) {
        this.oilQuantity = oilQuantity;
    }

    public String getBerthCode() {
        return berthCode;
    }

    public void setBerthCode(String berthCode) {
        this.berthCode = berthCode;
    }

    public String getWorkWindowId() {
        return workWindowId;
    }

    public void setWorkWindowId(String workWindowId) {
        this.workWindowId = workWindowId;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Integer getCertReviewStatus() {
        return certReviewStatus;
    }

    public void setCertReviewStatus(Integer certReviewStatus) {
        this.certReviewStatus = certReviewStatus;
    }

    public String getCertReviewComment() {
        return certReviewComment;
    }

    public void setCertReviewComment(String certReviewComment) {
        this.certReviewComment = certReviewComment;
    }

    public Date getCertReviewTime() {
        return certReviewTime;
    }

    public void setCertReviewTime(Date certReviewTime) {
        this.certReviewTime = certReviewTime;
    }

    public String getCertReviewer() {
        return certReviewer;
    }

    public void setCertReviewer(String certReviewer) {
        this.certReviewer = certReviewer;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Integer getSulfurCheckResult() {
        return sulfurCheckResult;
    }

    public void setSulfurCheckResult(Integer sulfurCheckResult) {
        this.sulfurCheckResult = sulfurCheckResult;
    }

    public Integer getWindowConflictFlag() {
        return windowConflictFlag;
    }

    public void setWindowConflictFlag(Integer windowConflictFlag) {
        this.windowConflictFlag = windowConflictFlag;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}
