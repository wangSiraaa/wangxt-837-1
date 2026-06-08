package com.maritime.bunkering;

import com.maritime.bunkering.dto.BunkeringApplySubmitDTO;
import com.maritime.bunkering.entity.BunkeringApply;
import com.maritime.bunkering.entity.OilBatch;
import com.maritime.bunkering.enums.ApplyStatusEnum;
import com.maritime.bunkering.rule.SulfurContentValidator;
import com.maritime.bunkering.service.BunkeringApplyService;
import com.maritime.bunkering.service.OilBatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("船舶燃油加注合规 - 验收测试")
class BunkeringAcceptanceTest {

    @Autowired
    private OilBatchService oilBatchService;

    @Autowired
    private BunkeringApplyService bunkeringApplyService;

    @Autowired
    private SulfurContentValidator sulfurContentValidator;

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    @DisplayName("验收路径：使用超标油品提交加注并验证无法确认")
    void testSubmitWithExceedSulfurOil_ShouldBeRejectedAndCannotConfirm() {
        String exceedBatchId = "BATCH003";

        OilBatch exceedBatch = oilBatchService.getById(exceedBatchId);
        assertNotNull(exceedBatch, "超标油品批次应存在");
        assertTrue(exceedBatch.getSulfurContent().compareTo(new BigDecimal("0.5")) > 0,
                "BATCH003硫含量应超标(实际: " + exceedBatch.getSulfurContent() + "%)");

        SulfurContentValidator.ValidationResult sulfurCheck = sulfurContentValidator.validate(exceedBatchId);
        assertFalse(sulfurCheck.isCompliant(), "硫含量检查应不通过");
        assertTrue(sulfurCheck.getMessage().contains("超标"),
                "检查结果应包含'超标'提示，实际: " + sulfurCheck.getMessage());

        BunkeringApplySubmitDTO dto = new BunkeringApplySubmitDTO();
        dto.setShipCode("SHIP001");
        dto.setShipName("远洋号");
        dto.setAgentCode("AGENT001");
        dto.setAgentName("上海外轮代理有限公司");
        dto.setSupplierCode("SUPP002");
        dto.setSupplierName("某地方炼油厂");
        dto.setOilBatchId(exceedBatchId);
        dto.setOilType("燃料油");
        dto.setOilQuantity(new BigDecimal("300.00"));
        dto.setBerthCode("BERTH002");
        dto.setPlanStartTime(toDate(LocalDateTime.of(2024, 6, 10, 9, 0, 0)));
        dto.setPlanEndTime(toDate(LocalDateTime.of(2024, 6, 10, 11, 0, 0)));
        dto.setCreateBy("agent001");

        BunkeringApply apply = null;
        try {
            apply = bunkeringApplyService.submitApply(dto);
            fail("提交超标油品申请应抛出异常");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("硫含量") || e.getMessage().contains("超标"),
                    "异常信息应包含硫含量超标提示，实际: " + e.getMessage());
        }

        java.util.List<BunkeringApply> applies = bunkeringApplyService.queryPage(1, 10, "SHIP001", null).getRecords();
        BunkeringApply rejectedApply = applies.stream()
                .filter(a -> exceedBatchId.equals(a.getOilBatchId()))
                .findFirst()
                .orElse(null);
        assertNotNull(rejectedApply, "应能查询到已拒绝的申请");
        assertEquals(ApplyStatusEnum.REJECTED.getCode(), rejectedApply.getApplyStatus(),
                "申请状态应为已拒绝(6)，实际: " + rejectedApply.getApplyStatus());
        assertNotNull(rejectedApply.getRejectReason(), "应记录拒绝原因");
        assertTrue(rejectedApply.getRejectReason().contains("硫含量"),
                "拒绝原因应包含硫含量，实际: " + rejectedApply.getRejectReason());
        assertEquals(Integer.valueOf(2), rejectedApply.getSulfurCheckResult(),
                "硫含量检查结果应为不合格(2)，实际: " + rejectedApply.getSulfurCheckResult());

        final String applyId = rejectedApply.getId();
        try {
            bunkeringApplyService.confirmApply(applyId, "admin");
            fail("确认已拒绝的申请应抛出异常");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("已拒绝") || e.getMessage().contains("状态"),
                    "异常信息应提示申请已拒绝，实际: " + e.getMessage());
        }

        BunkeringApply afterConfirm = bunkeringApplyService.getById(applyId);
        assertEquals(ApplyStatusEnum.REJECTED.getCode(), afterConfirm.getApplyStatus(),
                "确认失败后状态仍应为已拒绝，实际: " + afterConfirm.getApplyStatus());

        System.out.println("========== 验收测试通过 ==========");
        System.out.println("1. ✓ 超标油品BATCH003硫含量: " + exceedBatch.getSulfurContent() + "% > 0.5%");
        System.out.println("2. ✓ 提交申请被正确拦截");
        System.out.println("3. ✓ 申请状态: " + rejectedApply.getApplyStatus() + " (已拒绝)");
        System.out.println("4. ✓ 拒绝原因: " + rejectedApply.getRejectReason());
        System.out.println("5. ✓ 尝试确认被拒绝，状态保持已拒绝");
        System.out.println("=================================");
    }

    @Test
    @DisplayName("合格油品可以正常提交")
    void testSubmitWithQualifiedOil_ShouldSuccess() {
        String qualifiedBatchId = "BATCH001";

        OilBatch qualifiedBatch = oilBatchService.getById(qualifiedBatchId);
        assertNotNull(qualifiedBatch, "合格油品批次应存在");
        assertTrue(qualifiedBatch.getSulfurContent().compareTo(new BigDecimal("0.5")) < 0,
                "BATCH001硫含量应合格(实际: " + qualifiedBatch.getSulfurContent() + "%)");

        BunkeringApplySubmitDTO dto = new BunkeringApplySubmitDTO();
        dto.setShipCode("SHIP001");
        dto.setShipName("远洋号");
        dto.setAgentCode("AGENT001");
        dto.setAgentName("上海外轮代理有限公司");
        dto.setSupplierCode("SUPP001");
        dto.setSupplierName("中石化燃料油销售有限公司");
        dto.setOilBatchId(qualifiedBatchId);
        dto.setOilType("燃料油");
        dto.setOilQuantity(new BigDecimal("500.00"));
        dto.setBerthCode("BERTH002");
        dto.setWorkWindowId("WIN005");
        dto.setPlanStartTime(toDate(LocalDateTime.of(2024, 6, 10, 13, 0, 0)));
        dto.setPlanEndTime(toDate(LocalDateTime.of(2024, 6, 10, 17, 0, 0)));
        dto.setCreateBy("agent001");

        BunkeringApply apply = bunkeringApplyService.submitApply(dto);
        assertNotNull(apply, "提交成功应返回申请对象");
        assertEquals(ApplyStatusEnum.SUBMITTED.getCode(), apply.getApplyStatus(),
                "合格油品提交后状态应为已提交(1)，实际: " + apply.getApplyStatus());
        assertEquals(Integer.valueOf(1), apply.getSulfurCheckResult(),
                "硫含量检查结果应为合格(1)，实际: " + apply.getSulfurCheckResult());

        System.out.println("合格油品提交测试通过:");
        System.out.println("  申请ID: " + apply.getId());
        System.out.println("  状态: " + apply.getApplyStatus() + " (已提交)");
        System.out.println("  油品: " + qualifiedBatch.getBatchNo() + ", 硫含量: " + qualifiedBatch.getSulfurContent() + "%");
    }

    @Test
    @DisplayName("证书过期拦截验证")
    void testSubmitWithExpiredCert_ShouldBeRejected() {
        BunkeringApplySubmitDTO dto = new BunkeringApplySubmitDTO();
        dto.setShipCode("SHIP002");
        dto.setShipName("江河号");
        dto.setAgentCode("AGENT002");
        dto.setAgentName("宁波外轮代理有限公司");
        dto.setSupplierCode("SUPP001");
        dto.setSupplierName("中石化燃料油销售有限公司");
        dto.setOilBatchId("BATCH001");
        dto.setOilType("燃料油");
        dto.setOilQuantity(new BigDecimal("200.00"));
        dto.setBerthCode("BERTH003");
        dto.setPlanStartTime(toDate(LocalDateTime.of(2024, 6, 10, 10, 0, 0)));
        dto.setPlanEndTime(toDate(LocalDateTime.of(2024, 6, 10, 12, 0, 0)));
        dto.setCreateBy("agent002");

        try {
            bunkeringApplyService.submitApply(dto);
            fail("过期证书的船舶提交申请应抛出异常");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("证书") || e.getMessage().contains("过期"),
                    "异常信息应包含证书过期提示，实际: " + e.getMessage());
        }

        java.util.List<BunkeringApply> applies = bunkeringApplyService.queryPage(1, 10, "SHIP002", null).getRecords();
        BunkeringApply rejectedApply = applies.stream()
                .filter(a -> "SHIP002".equals(a.getShipCode()))
                .findFirst()
                .orElse(null);
        assertNotNull(rejectedApply, "应能查询到已拒绝的申请");
        assertEquals(ApplyStatusEnum.REJECTED.getCode(), rejectedApply.getApplyStatus(),
                "过期证书申请状态应为已拒绝");
        assertNotNull(rejectedApply.getRejectReason(), "应记录拒绝原因");
        assertTrue(rejectedApply.getRejectReason().contains("证书") || rejectedApply.getRejectReason().contains("过期"),
                "拒绝原因应包含证书过期");

        System.out.println("证书过期拦截测试通过:");
        System.out.println("  船舶: SHIP002 (江河号)");
        System.out.println("  状态: " + rejectedApply.getApplyStatus() + " (已拒绝)");
        System.out.println("  拒绝原因: " + rejectedApply.getRejectReason());
    }

    @Test
    @DisplayName("确认操作事务验证：硫含量超标时拒绝确认并持久化状态")
    void testConfirmWithExceedSulfur_ShouldPersistRejectedStatus() {
        BunkeringApplySubmitDTO dto = new BunkeringApplySubmitDTO();
        dto.setShipCode("SHIP001");
        dto.setShipName("远洋号");
        dto.setAgentCode("AGENT001");
        dto.setAgentName("上海外轮代理有限公司");
        dto.setSupplierCode("SUPP001");
        dto.setSupplierName("中石化燃料油销售有限公司");
        dto.setOilBatchId("BATCH001");
        dto.setOilType("燃料油");
        dto.setOilQuantity(new BigDecimal("300.00"));
        dto.setBerthCode("BERTH002");
        dto.setWorkWindowId("WIN005");
        dto.setPlanStartTime(toDate(LocalDateTime.of(2024, 6, 10, 13, 0, 0)));
        dto.setPlanEndTime(toDate(LocalDateTime.of(2024, 6, 10, 17, 0, 0)));
        dto.setCreateBy("agent001");

        BunkeringApply apply = bunkeringApplyService.submitApply(dto);
        assertNotNull(apply, "提交成功应返回申请对象");
        assertEquals(ApplyStatusEnum.SUBMITTED.getCode(), apply.getApplyStatus(),
                "提交后状态应为已提交(1)");

        String applyId = apply.getId();

        BunkeringApply updateApply = new BunkeringApply();
        updateApply.setId(applyId);
        updateApply.setApplyStatus(ApplyStatusEnum.OIL_CHECK_PASSED.getCode());
        updateApply.setSulfurCheckResult(2);
        updateApply.setUpdateBy("test");
        bunkeringApplyService.updateById(updateApply);

        BunkeringApply beforeConfirm = bunkeringApplyService.getById(applyId);
        assertEquals(ApplyStatusEnum.OIL_CHECK_PASSED.getCode(), beforeConfirm.getApplyStatus(),
                "确认前状态应为油品检测通过(3)");
        assertEquals(Integer.valueOf(2), beforeConfirm.getSulfurCheckResult(),
                "确认前硫含量检查结果应为不合格(2)");

        try {
            bunkeringApplyService.confirmApply(applyId, "admin");
            fail("硫含量超标时确认应抛出异常");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("硫含量") || e.getMessage().contains("超标"),
                    "异常信息应包含硫含量超标提示，实际: " + e.getMessage());
        }

        BunkeringApply afterConfirm = bunkeringApplyService.getById(applyId);
        assertEquals(ApplyStatusEnum.REJECTED.getCode(), afterConfirm.getApplyStatus(),
                "硫含量超标时确认后状态应持久化为已拒绝(6)，实际: " + afterConfirm.getApplyStatus());
        assertEquals(Integer.valueOf(1), afterConfirm.getWindowConflictFlag(),
                "windowConflictFlag应设置为1，实际: " + afterConfirm.getWindowConflictFlag());
        assertNotNull(afterConfirm.getRejectReason(), "应写入rejectReason");
        assertTrue(afterConfirm.getRejectReason().contains("硫含量") || afterConfirm.getRejectReason().contains("超标"),
                "rejectReason应包含硫含量超标，实际: " + afterConfirm.getRejectReason());

        System.out.println("========== 确认操作硫含量超标事务验证通过 ==========");
        System.out.println("1. ✓ 确认前状态: " + beforeConfirm.getApplyStatus() + " (油品检测通过)");
        System.out.println("2. ✓ 确认前硫含量检查结果: " + beforeConfirm.getSulfurCheckResult() + " (不合格)");
        System.out.println("3. ✓ 确认抛出异常: " + "硫含量超标，拒绝确认");
        System.out.println("4. ✓ 确认后状态: " + afterConfirm.getApplyStatus() + " (已拒绝) - 已持久化");
        System.out.println("5. ✓ windowConflictFlag: " + afterConfirm.getWindowConflictFlag() + " - 已持久化");
        System.out.println("6. ✓ rejectReason: " + afterConfirm.getRejectReason() + " - 已持久化");
        System.out.println("=====================================================");
    }

    @Test
    @DisplayName("确认操作事务验证：作业窗口冲突时拒绝确认并持久化状态")
    void testConfirmWithWindowConflict_ShouldPersistRejectedStatus() {
        BunkeringApplySubmitDTO dto = new BunkeringApplySubmitDTO();
        dto.setShipCode("SHIP003");
        dto.setShipName("测试船3号");
        dto.setAgentCode("AGENT001");
        dto.setAgentName("上海外轮代理有限公司");
        dto.setSupplierCode("SUPP001");
        dto.setSupplierName("中石化燃料油销售有限公司");
        dto.setOilBatchId("BATCH001");
        dto.setOilType("燃料油");
        dto.setOilQuantity(new BigDecimal("200.00"));
        dto.setBerthCode("BERTH001");
        dto.setPlanStartTime(toDate(LocalDateTime.of(2024, 6, 11, 8, 0, 0)));
        dto.setPlanEndTime(toDate(LocalDateTime.of(2024, 6, 11, 12, 0, 0)));
        dto.setCreateBy("agent001");

        BunkeringApply apply = null;
        try {
            apply = bunkeringApplyService.submitApply(dto);
            fail("窗口冲突时提交应被拒绝");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("冲突") || e.getMessage().contains("靠泊"),
                    "异常信息应包含窗口冲突提示，实际: " + e.getMessage());
        }

        java.util.List<BunkeringApply> applies = bunkeringApplyService.queryPage(1, 10, "SHIP003", null).getRecords();
        BunkeringApply submittedApply = applies.stream()
                .filter(a -> "SHIP003".equals(a.getShipCode()))
                .findFirst()
                .orElse(null);
        assertNotNull(submittedApply, "应能查询到申请");

        String applyId = submittedApply.getId();

        BunkeringApply updateApply = new BunkeringApply();
        updateApply.setId(applyId);
        updateApply.setApplyStatus(ApplyStatusEnum.OIL_CHECK_PASSED.getCode());
        updateApply.setSulfurCheckResult(1);
        updateApply.setRejectReason(null);
        updateApply.setWindowConflictFlag(0);
        updateApply.setUpdateBy("test");
        bunkeringApplyService.updateById(updateApply);

        BunkeringApply beforeConfirm = bunkeringApplyService.getById(applyId);
        assertEquals(ApplyStatusEnum.OIL_CHECK_PASSED.getCode(), beforeConfirm.getApplyStatus(),
                "确认前状态应为油品检测通过(3)");

        try {
            bunkeringApplyService.confirmApply(applyId, "admin");
            fail("窗口冲突时确认应抛出异常");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("冲突") || e.getMessage().contains("靠泊"),
                    "异常信息应包含窗口冲突提示，实际: " + e.getMessage());
        }

        BunkeringApply afterConfirm = bunkeringApplyService.getById(applyId);
        assertEquals(ApplyStatusEnum.REJECTED.getCode(), afterConfirm.getApplyStatus(),
                "窗口冲突时确认后状态应持久化为已拒绝(6)，实际: " + afterConfirm.getApplyStatus());
        assertEquals(Integer.valueOf(1), afterConfirm.getWindowConflictFlag(),
                "windowConflictFlag应设置为1，实际: " + afterConfirm.getWindowConflictFlag());
        assertNotNull(afterConfirm.getRejectReason(), "应写入rejectReason");
        assertTrue(afterConfirm.getRejectReason().contains("冲突") || afterConfirm.getRejectReason().contains("靠泊"),
                "rejectReason应包含窗口冲突，实际: " + afterConfirm.getRejectReason());

        System.out.println("========== 确认操作窗口冲突事务验证通过 ==========");
        System.out.println("1. ✓ 确认前状态: " + beforeConfirm.getApplyStatus() + " (油品检测通过)");
        System.out.println("2. ✓ 确认抛出异常: 作业窗口与靠泊计划冲突");
        System.out.println("3. ✓ 确认后状态: " + afterConfirm.getApplyStatus() + " (已拒绝) - 已持久化");
        System.out.println("4. ✓ windowConflictFlag: " + afterConfirm.getWindowConflictFlag() + " - 已持久化");
        System.out.println("5. ✓ rejectReason: " + afterConfirm.getRejectReason() + " - 已持久化");
        System.out.println("====================================================");
    }

}
