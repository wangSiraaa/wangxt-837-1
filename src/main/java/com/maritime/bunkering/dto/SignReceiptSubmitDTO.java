package com.maritime.bunkering.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class SignReceiptSubmitDTO {

    @NotBlank(message = "加注申请ID不能为空")
    private String applyId;

    @NotNull(message = "实际加注量不能为空")
    private BigDecimal actualQuantity;

    @NotNull(message = "签收时间不能为空")
    private Date signTime;

    @NotBlank(message = "签收人不能为空")
    private String signer;

    private String remark;

    @NotBlank(message = "操作人不能为空")
    private String operator;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
