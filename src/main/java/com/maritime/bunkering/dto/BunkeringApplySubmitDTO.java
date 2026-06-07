package com.maritime.bunkering.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class BunkeringApplySubmitDTO {

    @NotBlank(message = "船舶编号不能为空")
    private String shipCode;

    @NotBlank(message = "船舶名称不能为空")
    private String shipName;

    @NotBlank(message = "船舶代理编号不能为空")
    private String agentCode;

    @NotBlank(message = "船舶代理名称不能为空")
    private String agentName;

    @NotBlank(message = "供油企业编号不能为空")
    private String supplierCode;

    @NotBlank(message = "供油企业名称不能为空")
    private String supplierName;

    @NotBlank(message = "油品批次ID不能为空")
    private String oilBatchId;

    @NotBlank(message = "油品类型不能为空")
    private String oilType;

    @NotNull(message = "加油数量不能为空")
    private BigDecimal oilQuantity;

    @NotBlank(message = "泊位编号不能为空")
    private String berthCode;

    private String workWindowId;

    @NotNull(message = "计划开始时间不能为空")
    private Date planStartTime;

    @NotNull(message = "计划结束时间不能为空")
    private Date planEndTime;

    private String createBy;

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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}
