package com.maritime.bunkering.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("sign_receipt")
public class SignReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String receiptNo;

    private String applyId;

    private String applyNo;

    private String shipCode;

    private String shipName;

    private String supplierCode;

    private String supplierName;

    private BigDecimal actualQuantity;

    private Date signTime;

    private String signer;

    private Integer beforeStatus;

    private Integer afterStatus;

    private Integer certCheckResult;

    private String certCheckMsg;

    private Integer oilCheckResult;

    private String oilCheckMsg;

    private Integer windowCheckResult;

    private String windowCheckMsg;

    private Integer receiptStatus;

    private String rejectReason;

    private String remark;

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

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

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

    public BigDecimal getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(BigDecimal actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public Integer getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(Integer beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Integer getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Integer afterStatus) {
        this.afterStatus = afterStatus;
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

    public Integer getOilCheckResult() {
        return oilCheckResult;
    }

    public void setOilCheckResult(Integer oilCheckResult) {
        this.oilCheckResult = oilCheckResult;
    }

    public String getOilCheckMsg() {
        return oilCheckMsg;
    }

    public void setOilCheckMsg(String oilCheckMsg) {
        this.oilCheckMsg = oilCheckMsg;
    }

    public Integer getWindowCheckResult() {
        return windowCheckResult;
    }

    public void setWindowCheckResult(Integer windowCheckResult) {
        this.windowCheckResult = windowCheckResult;
    }

    public String getWindowCheckMsg() {
        return windowCheckMsg;
    }

    public void setWindowCheckMsg(String windowCheckMsg) {
        this.windowCheckMsg = windowCheckMsg;
    }

    public Integer getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(Integer receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
