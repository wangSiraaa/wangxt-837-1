package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.entity.OilBatch;
import com.maritime.bunkering.mapper.OilBatchMapper;
import com.maritime.bunkering.rule.SulfurContentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OilBatchService {

    @Autowired
    private OilBatchMapper oilBatchMapper;

    @Autowired
    private SulfurContentValidator sulfurContentValidator;

    public OilBatch getById(String id) {
        OilBatch batch = oilBatchMapper.selectById(id);
        if (batch == null) {
            throw new BusinessException("油品批次不存在: " + id);
        }
        return batch;
    }

    public OilBatch getByBatchNo(String batchNo) {
        return oilBatchMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OilBatch>()
                        .eq("batch_no", batchNo)
                        .eq("deleted", 0)
        );
    }

    public IPage<OilBatch> queryPage(int pageNum, int pageSize, String oilType, Integer batchStatus) {
        Page<OilBatch> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OilBatch> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (oilType != null && !oilType.isEmpty()) {
            wrapper.eq("oil_type", oilType);
        }
        if (batchStatus != null) {
            wrapper.eq("batch_status", batchStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return oilBatchMapper.selectPage(page, wrapper);
    }

    public List<OilBatch> queryNonCompliantBatches() {
        return oilBatchMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OilBatch>()
                        .eq("batch_status", 2)
                        .eq("deleted", 0)
                        .orderByDesc("create_time")
        );
    }

    public SulfurContentValidator.ValidationResult checkSulfurContent(String batchId) {
        return sulfurContentValidator.validate(batchId);
    }

    public OilBatch create(OilBatch batch) {
        if (batch.getId() == null || batch.getId().isEmpty()) {
            batch.setId("BATCH" + System.currentTimeMillis());
        }
        if (batch.getBatchStatus() == null) {
            if (batch.getSulfurContent() != null &&
                    batch.getSulfurContent().compareTo(new BigDecimal("0.5")) > 0) {
                batch.setBatchStatus(2);
            } else {
                batch.setBatchStatus(1);
            }
        }
        oilBatchMapper.insert(batch);
        return batch;
    }

    public OilBatch update(OilBatch batch) {
        OilBatch existing = oilBatchMapper.selectById(batch.getId());
        if (existing == null) {
            throw new BusinessException("油品批次不存在: " + batch.getId());
        }
        if (batch.getSulfurContent() != null) {
            if (batch.getSulfurContent().compareTo(new BigDecimal("0.5")) > 0) {
                batch.setBatchStatus(2);
            } else {
                batch.setBatchStatus(1);
            }
        }
        oilBatchMapper.updateById(batch);
        return oilBatchMapper.selectById(batch.getId());
    }
}
