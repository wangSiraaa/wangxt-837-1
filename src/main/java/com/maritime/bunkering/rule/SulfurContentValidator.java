package com.maritime.bunkering.rule;

import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.entity.OilBatch;
import com.maritime.bunkering.mapper.OilBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SulfurContentValidator {

    @Value("${bunkering.sulfur-limit:0.5}")
    private BigDecimal sulfurLimit;

    @Autowired
    private OilBatchMapper oilBatchMapper;

    public ValidationResult validate(String oilBatchId) {
        OilBatch batch = oilBatchMapper.selectById(oilBatchId);
        if (batch == null) {
            throw new BusinessException("油品批次不存在: " + oilBatchId);
        }

        ValidationResult result = new ValidationResult();
        result.setBatchId(batch.getId());
        result.setBatchNo(batch.getBatchNo());
        result.setSulfurContent(batch.getSulfurContent());
        result.setSulfurLimit(sulfurLimit);

        if (batch.getSulfurContent() == null) {
            throw new BusinessException("油品批次【" + batch.getBatchNo() + "】硫含量数据缺失");
        }

        if (batch.getSulfurContent().compareTo(sulfurLimit) > 0) {
            result.setCompliant(false);
            result.setMessage(
                    "油品硫含量超标，拦截加注申请。" +
                            "批次号: " + batch.getBatchNo() +
                            "，硫含量: " + batch.getSulfurContent() + "%，" +
                            "限值: " + sulfurLimit + "%，" +
                            "超出: " + batch.getSulfurContent().subtract(sulfurLimit) + "%"
            );
        } else {
            result.setCompliant(true);
            result.setMessage(
                    "油品硫含量合格。批次号: " + batch.getBatchNo() +
                            "，硫含量: " + batch.getSulfurContent() + "%，" +
                            "限值: " + sulfurLimit + "%"
            );
        }

        return result;
    }

    public static class ValidationResult {
        private String batchId;
        private String batchNo;
        private BigDecimal sulfurContent;
        private BigDecimal sulfurLimit;
        private boolean compliant;
        private String message;

        public String getBatchId() { return batchId; }
        public void setBatchId(String batchId) { this.batchId = batchId; }
        public String getBatchNo() { return batchNo; }
        public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
        public BigDecimal getSulfurContent() { return sulfurContent; }
        public void setSulfurContent(BigDecimal sulfurContent) { this.sulfurContent = sulfurContent; }
        public BigDecimal getSulfurLimit() { return sulfurLimit; }
        public void setSulfurLimit(BigDecimal sulfurLimit) { this.sulfurLimit = sulfurLimit; }
        public boolean isCompliant() { return compliant; }
        public void setCompliant(boolean compliant) { this.compliant = compliant; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
