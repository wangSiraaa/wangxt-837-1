#!/bin/bash

set -e

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "  船舶燃油加注合规 API 服务 - 确认操作事务验证脚本"
echo "  验证：确认操作事务处理 - 状态持久化验证"
echo "=========================================="
echo ""

echo "等待服务启动..."
MAX_RETRIES=30
RETRY=0
while [ $RETRY -lt $MAX_RETRIES ]; do
    if curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/bunkering/agent/apply/APPLY001" | grep -q "200"; then
        echo "服务已启动成功！"
        break
    fi
    RETRY=$((RETRY + 1))
    echo "  等待中... ($RETRY/$MAX_RETRIES)"
    sleep 2
done

if [ $RETRY -eq $MAX_RETRIES ]; then
    echo "错误：服务启动超时，请检查服务是否正常运行"
    exit 1
fi

echo ""
echo "=========================================="
echo "场景 1: 硫含量超标 - 确认操作拒绝并持久化状态"
echo "  测试数据: APPLY003 (状态=油品检测通过, 硫含量检查结果=不合格)"
echo "=========================================="

echo ""
echo "步骤 1.1: 查询确认前状态"
BEFORE_CONFIRM1=$(curl -s "$BASE_URL/api/bunkering/agent/apply/APPLY003")
BEFORE_STATUS1=$(echo "$BEFORE_CONFIRM1" | jq -r '.data.applyStatus')
BEFORE_SULFUR1=$(echo "$BEFORE_CONFIRM1" | jq -r '.data.sulfurCheckResult')
BEFORE_WINDOW1=$(echo "$BEFORE_CONFIRM1" | jq -r '.data.windowConflictFlag')
BEFORE_REJECT1=$(echo "$BEFORE_CONFIRM1" | jq -r '.data.rejectReason')
echo "确认前状态: $BEFORE_STATUS1 (3=油品检测通过)"
echo "确认前硫含量检查结果: $BEFORE_SULFUR1 (2=不合格)"
echo "确认前windowConflictFlag: $BEFORE_WINDOW1 (0=无冲突)"
echo "确认前rejectReason: $BEFORE_REJECT1 (null)"

echo ""
echo "步骤 1.2: 执行确认操作（预期被拒绝）"
CONFIRM_RESPONSE1=$(curl -s -X POST \
  "$BASE_URL/api/bunkering/agent/apply/confirm/APPLY003?operator=admin")
echo "确认响应: $CONFIRM_RESPONSE1"
CONFIRM_CODE1=$(echo "$CONFIRM_RESPONSE1" | jq -r '.code')
CONFIRM_MSG1=$(echo "$CONFIRM_RESPONSE1" | jq -r '.message')

if [ "$CONFIRM_CODE1" = "9" ]; then
    echo "✓ 确认操作被正确拒绝，返回错误码: $CONFIRM_CODE1"
    echo "  错误信息: $CONFIRM_MSG1"
else
    echo "✗ 确认操作未被拒绝，返回码: $CONFIRM_CODE1"
    exit 1
fi

echo ""
echo "步骤 1.3: 验证状态已持久化到数据库（关键：事务不回滚）"
AFTER_CONFIRM1=$(curl -s "$BASE_URL/api/bunkering/agent/apply/APPLY003")
AFTER_STATUS1=$(echo "$AFTER_CONFIRM1" | jq -r '.data.applyStatus')
AFTER_WINDOW1=$(echo "$AFTER_CONFIRM1" | jq -r '.data.windowConflictFlag')
AFTER_REJECT1=$(echo "$AFTER_CONFIRM1" | jq -r '.data.rejectReason')

echo "确认后状态: $AFTER_STATUS1 (6=已拒绝)"
echo "windowConflictFlag: $AFTER_WINDOW1 (1=冲突)"
echo "rejectReason: $AFTER_REJECT1"

TEST1_PASS=true
if [ "$AFTER_STATUS1" != "6" ]; then
    echo "✗ 确认后状态未持久化为已拒绝(6)，实际: $AFTER_STATUS1"
    TEST1_PASS=false
fi
if [ "$AFTER_WINDOW1" != "1" ]; then
    echo "✗ windowConflictFlag 未持久化为1，实际: $AFTER_WINDOW1"
    TEST1_PASS=false
fi
if [ "$AFTER_REJECT1" = "null" ] || [ -z "$AFTER_REJECT1" ]; then
    echo "✗ rejectReason 未写入"
    TEST1_PASS=false
fi
if [[ "$AFTER_REJECT1" != *"硫含量"* ]] && [[ "$AFTER_REJECT1" != *"超标"* ]]; then
    echo "✗ rejectReason 不包含硫含量超标信息，实际: $AFTER_REJECT1"
    TEST1_PASS=false
fi

if [ "$TEST1_PASS" = true ]; then
    echo "✓ 确认后状态已正确持久化，事务未回滚"
else
    exit 1
fi

echo ""
echo "=========================================="
echo "场景 2: 作业窗口冲突 - 确认操作拒绝并持久化状态"
echo "  测试数据: APPLY004 (状态=油品检测通过, 窗口与靠泊计划冲突)"
echo "=========================================="

echo ""
echo "步骤 2.1: 查询确认前状态"
BEFORE_CONFIRM2=$(curl -s "$BASE_URL/api/bunkering/agent/apply/APPLY004")
BEFORE_STATUS2=$(echo "$BEFORE_CONFIRM2" | jq -r '.data.applyStatus')
BEFORE_WINDOW2=$(echo "$BEFORE_CONFIRM2" | jq -r '.data.windowConflictFlag')
BEFORE_REJECT2=$(echo "$BEFORE_CONFIRM2" | jq -r '.data.rejectReason')
echo "确认前状态: $BEFORE_STATUS2 (3=油品检测通过)"
echo "确认前windowConflictFlag: $BEFORE_WINDOW2 (0=无冲突)"
echo "确认前rejectReason: $BEFORE_REJECT2 (null)"

echo ""
echo "步骤 2.2: 执行确认操作（预期被拒绝）"
CONFIRM_RESPONSE2=$(curl -s -X POST \
  "$BASE_URL/api/bunkering/agent/apply/confirm/APPLY004?operator=admin")
echo "确认响应: $CONFIRM_RESPONSE2"
CONFIRM_CODE2=$(echo "$CONFIRM_RESPONSE2" | jq -r '.code')
CONFIRM_MSG2=$(echo "$CONFIRM_RESPONSE2" | jq -r '.message')

if [ "$CONFIRM_CODE2" = "9" ]; then
    echo "✓ 确认操作被正确拒绝，返回错误码: $CONFIRM_CODE2"
    echo "  错误信息: $CONFIRM_MSG2"
else
    echo "✗ 确认操作未被拒绝，返回码: $CONFIRM_CODE2"
    exit 1
fi

echo ""
echo "步骤 2.3: 验证状态已持久化到数据库（关键：事务不回滚）"
AFTER_CONFIRM2=$(curl -s "$BASE_URL/api/bunkering/agent/apply/APPLY004")
AFTER_STATUS2=$(echo "$AFTER_CONFIRM2" | jq -r '.data.applyStatus')
AFTER_WINDOW2=$(echo "$AFTER_CONFIRM2" | jq -r '.data.windowConflictFlag')
AFTER_REJECT2=$(echo "$AFTER_CONFIRM2" | jq -r '.data.rejectReason')

echo "确认后状态: $AFTER_STATUS2 (6=已拒绝)"
echo "windowConflictFlag: $AFTER_WINDOW2 (1=冲突)"
echo "rejectReason: $AFTER_REJECT2"

TEST2_PASS=true
if [ "$AFTER_STATUS2" != "6" ]; then
    echo "✗ 确认后状态未持久化为已拒绝(6)，实际: $AFTER_STATUS2"
    TEST2_PASS=false
fi
if [ "$AFTER_WINDOW2" != "1" ]; then
    echo "✗ windowConflictFlag 未持久化为1，实际: $AFTER_WINDOW2"
    TEST2_PASS=false
fi
if [ "$AFTER_REJECT2" = "null" ] || [ -z "$AFTER_REJECT2" ]; then
    echo "✗ rejectReason 未写入"
    TEST2_PASS=false
fi
if [[ "$AFTER_REJECT2" != *"冲突"* ]] && [[ "$AFTER_REJECT2" != *"靠泊"* ]]; then
    echo "✗ rejectReason 不包含窗口冲突信息，实际: $AFTER_REJECT2"
    TEST2_PASS=false
fi

if [ "$TEST2_PASS" = true ]; then
    echo "✓ 确认后状态已正确持久化，事务未回滚"
else
    exit 1
fi

echo ""
echo "=========================================="
echo "  确认操作事务验证完成！"
echo "=========================================="
echo ""
echo "✓ 所有验证步骤通过"
echo ""
echo "核心验证点："
echo "  1. 硫含量超标时，确认操作拒绝，状态持久化为已拒绝(6)"
echo "  2. windowConflictFlag=1 已持久化"
echo "  3. rejectReason 已写入并持久化"
echo "  4. 作业窗口冲突时，确认操作拒绝，状态持久化为已拒绝(6)"
echo "  5. 事务不会因为抛出 BusinessException 而回滚"
echo ""
echo "事务修复验证："
echo "  ✓ @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)"
echo "  ✓ updateById 执行后抛出 BusinessException 不会回滚"
echo ""
echo "执行流程："
echo "  开始事务"
echo "    ↓"
echo "  查询申请信息"
echo "    ↓"
echo "  业务规则校验（硫含量超标/窗口冲突）"
echo "    ↓"
echo "  updateById 更新: applyStatus=6, windowConflictFlag=1, rejectReason='...'"
echo "    ↓"
echo "  throw BusinessException"
echo "    ↓"
echo "  noRollbackFor 生效 → 事务提交 → 数据持久化 ✅"
echo ""
exit 0
