# 船舶燃油加注合规 API 服务

## 项目简介

船舶燃油加注合规管理系统，实现船舶代理提交加注申请、供油企业复核船舶证书、港航监管读取油品批次结果的全流程业务管理。系统严格执行三类合规校验，确保船舶燃油加注作业安全环保。

## 业务边界

| 规则 | 业务描述 | 处理方式 |
|------|---------|---------|
| 证书过期 | 船舶证书过期或失效 | 拦截加注申请，状态改为已拒绝 |
| 硫含量超标 | 油品硫含量 > 0.5% | 拦截加注申请，明确提示超标信息 |
| 窗口冲突 | 作业时间不在可用窗口内或与靠泊计划冲突 | 拒绝确认，改变申请状态 |

## 技术栈

- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis Plus 3.5.3.1
- **数据库**: H2 内存数据库 2.1.214
- **构建工具**: Maven 3.6+
- **JDK版本**: Java 1.8

## 项目结构

```
src/main/java/com/maritime/bunkering/
├── BunkeringComplianceApplication.java    # 启动类
├── common/                                  # 公共模块
│   ├── Result.java                         # 统一响应格式
│   ├── BusinessException.java              # 业务异常
│   └── GlobalExceptionHandler.java         # 全局异常处理
├── config/                                  # 配置类
│   └── MybatisPlusConfig.java              # MyBatis Plus 配置
├── controller/                              # API 控制层
│   ├── BunkeringAgentController.java       # 船舶代理接口
│   ├── OilSupplierController.java          # 供油企业接口（含签收提交）
│   ├── PortAuthorityController.java        # 港航监管接口（含签收核对）
│   └── BaseDataController.java             # 基础数据接口
├── dto/                                     # 数据传输对象
│   ├── BunkeringApplySubmitDTO.java        # 加注申请提交参数
│   ├── CertReviewDTO.java                  # 证书复核参数
│   ├── SignReceiptSubmitDTO.java           # 签收单提交参数
│   └── SignCheckResultVO.java              # 签收核对结果返回
├── entity/                                  # 实体类
│   ├── ShipCert.java                       # 船舶证书
│   ├── OilBatch.java                       # 油品批次
│   ├── WorkWindow.java                     # 作业窗口
│   ├── BerthPlan.java                      # 靠泊计划
│   ├── BunkeringApply.java                 # 加注申请
│   └── SignReceipt.java                    # 签收单（新增）
├── enums/                                   # 枚举类
│   ├── ApplyStatusEnum.java                # 申请状态枚举（含签收状态）
│   └── CertReviewStatusEnum.java           # 证书复核状态枚举
├── mapper/                                  # 数据访问层
│   ├── ShipCertMapper.java
│   ├── OilBatchMapper.java
│   ├── WorkWindowMapper.java
│   ├── BerthPlanMapper.java
│   ├── BunkeringApplyMapper.java
│   └── SignReceiptMapper.java              # 签收单数据访问（新增）
├── rule/                                    # 业务规则校验器
│   ├── CertExpireValidator.java            # 证书过期校验
│   ├── SulfurContentValidator.java         # 硫含量超标校验
│   └── WorkWindowConflictValidator.java    # 作业窗口冲突校验
└── service/                                 # 业务逻辑层
    ├── BunkeringApplyService.java          # 加注申请服务
    ├── OilBatchService.java                # 油品批次服务
    ├── ShipCertService.java                # 船舶证书服务
    ├── WorkWindowService.java              # 作业窗口服务
    ├── BerthPlanService.java               # 靠泊计划服务
    └── SignReceiptService.java             # 签收单服务（新增）
```

## 状态机

```
草稿(0) → 已提交(1) → 证书复核通过(2) → 油品检测通过(3) → 待确认(4) → 已确认(5) → 待签收(9) → 已签收(10)
                  ↓               ↓                ↓                ↓                ↓
               已拒绝(6)       已拒绝(6)        已拒绝(6)        已拒绝(6)        签收拒绝(11)
```

## 快速开始

### 1. 编译项目

```bash
mvn clean compile
```

### 2. 启动服务

```bash
mvn spring-boot:run
```

服务启动后访问：
- 服务地址: http://localhost:8080
- H2控制台: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:bunkeringdb`
  - 用户名: `sa`
  - 密码: 空

### 3. 运行验收测试

方式一：运行单元测试

```bash
mvn test -Dtest=BunkeringAcceptanceTest
```

方式二：运行 Shell 验证脚本

```bash
chmod +x verify.sh
./verify.sh
```

## API 文档

### 船舶代理接口 (`/api/bunkering/agent`)

| 方法 | 路径 | 描述 |
|------|------|-----|
| POST | `/apply/submit` | 提交加注申请 |
| GET | `/apply/{id}` | 查询申请详情 |
| GET | `/apply/list` | 查询申请列表 |
| POST | `/apply/confirm/{id}` | 确认加注申请 |

**提交加注申请示例**:

```json
POST /api/bunkering/agent/apply/submit
Content-Type: application/json

{
  "shipCode": "SHIP001",
  "shipName": "远洋号",
  "agentCode": "AGENT001",
  "agentName": "上海外轮代理有限公司",
  "supplierCode": "SUPP001",
  "supplierName": "中石化燃料油销售有限公司",
  "oilBatchId": "BATCH001",
  "oilType": "燃料油",
  "oilQuantity": 500.00,
  "berthCode": "BERTH001",
  "workWindowId": "WIN006",
  "planStartTime": "2024-06-11T08:00:00",
  "planEndTime": "2024-06-11T12:00:00",
  "createBy": "agent001"
}
```

### 供油企业接口 (`/api/bunkering/supplier`)

| 方法 | 路径 | 描述 |
|------|------|-----|
| GET | `/apply/pending` | 查询待复核申请 |
| POST | `/cert/review` | 复核船舶证书 |
| GET | `/ship/certs/{shipCode}` | 查询船舶证书列表 |
| GET | `/apply/{id}` | 查询申请详情 |
| POST | `/sign/submit` | 提交签收单 |
| GET | `/sign/{id}` | 查询签收单详情 |
| GET | `/sign/list` | 查询签收单列表 |

**提交签收单示例**:

```json
POST /api/bunkering/supplier/sign/submit
Content-Type: application/json

{
  "applyId": "APPLY005",
  "actualQuantity": 498.50,
  "signTime": "2024-06-10T11:30:00",
  "signer": "张三",
  "remark": "加注作业完成，实际量与计划量略有差异",
  "operator": "supplier001"
}
```

### 港航监管接口 (`/api/bunkering/authority`)

| 方法 | 路径 | 描述 |
|------|------|-----|
| GET | `/oil-batch/{id}` | 查询油品批次 |
| GET | `/oil-batch/batchNo/{batchNo}` | 按批次号查询 |
| GET | `/oil-batch/list` | 查询油品批次列表 |
| GET | `/oil-batch/non-compliant` | 查询不合格油品 |
| GET | `/oil-batch/check-sulfur/{id}` | 检查硫含量 |
| GET | `/sign/check/{applyId}` | 查询签收核对结果 |
| GET | `/sign/{id}` | 查询签收单详情 |
| GET | `/sign/list` | 查询签收单列表 |
| GET | `/sign/history/{applyId}` | 查询申请签收历史 |

**查询签收核对结果示例**:

```json
GET /api/bunkering/authority/sign/check/APPLY005
```

返回数据包含：
- 船舶证书列表及核对结果
- 油品批次信息及硫含量检查结果
- 作业窗口信息及靠泊冲突检查结果
- 签收单信息（签收人、签收时间、实际加注量）
- 状态变化记录（签收前状态、签收后状态）

### 基础数据接口 (`/api/bunkering/base`)

提供船舶证书、油品批次、作业窗口、靠泊计划的 CRUD 接口。

## 初始化数据说明

系统启动时自动加载以下测试数据：

### 油品批次

| 批次号 | 油品类型 | 硫含量(%) | 状态 | 备注 |
|--------|---------|-----------|------|-----|
| BATCH001 | 燃料油 | 0.35 | 合格 | 符合标准 |
| BATCH002 | 柴油 | 0.10 | 合格 | 符合标准 |
| **BATCH003** | **燃料油** | **0.85** | **不合格** | **验收测试用（超标）** |
| BATCH004 | 燃料油 | 0.42 | 合格 | 符合标准 |

### 船舶证书

| 船舶 | 证书类型 | 有效期 | 状态 | 备注 |
|------|---------|--------|------|-----|
| SHIP001 远洋号 | 船舶检验证书 | 2025-12-31 | 有效 | 正常 |
| **SHIP002 江河号** | **船舶检验证书** | **2024-03-14** | **已过期** | **验收测试用（过期）** |

### 作业窗口与靠泊计划

预置了多个泊位的作业窗口和靠泊计划，用于窗口冲突测试。

## 验收路径

### 使用超标油品提交加注并验证无法确认

**步骤**:

1. **查询超标油品**
   - 访问: `GET /api/bunkering/authority/oil-batch/BATCH003`
   - 确认: 硫含量 = 0.85% > 0.5%，状态不合格

2. **检查硫含量**
   - 访问: `GET /api/bunkering/authority/oil-batch/check-sulfur/BATCH003`
   - 确认: `compliant = false`，消息包含"超标"

3. **提交超标油品加注申请**
   - 访问: `POST /api/bunkering/agent/apply/submit`
   - 参数: `oilBatchId = "BATCH003"`
   - 确认: 返回 `code = 9`（业务错误），消息包含"硫含量超标"

4. **验证申请状态为已拒绝**
   - 访问: `GET /api/bunkering/agent/apply/list?shipCode=SHIP001`
   - 确认: `applyStatus = 6`（已拒绝），`rejectReason` 包含硫含量信息

5. **尝试确认被拒绝的申请**
   - 访问: `POST /api/bunkering/agent/apply/confirm/{申请ID}`
   - 确认: 返回 `code = 9`，提示"申请已拒绝，无法确认"

**预期结果**: 所有验证步骤通过，硫含量超标拦截逻辑正确。

---

## 到港签收确认功能说明

### 业务流程
1. 供油企业完成加注作业后，提交签收单
2. 系统自动校验：船舶证书有效性、油品硫含量、靠泊计划冲突
3. 校验全部通过 → 签收成功，状态变更为"已签收"
4. 任一校验不通过 → 签收拒绝，状态变更为"签收拒绝"，记录拒绝原因
5. 港航监管可查看签收核对结果，包含三类校验的详细信息

### 校验规则
| 校验项 | 不通过条件 | 处理方式 |
|--------|-----------|---------|
| 船舶证书 | 证书过期或状态无效 | 签收拒绝 |
| 油品硫含量 | 硫含量 > 0.5% | 签收拒绝 |
| 靠泊计划 | 作业时间与靠泊计划冲突 | 签收拒绝 |

### 状态变化记录
签收单记录完整的状态变更轨迹：
- `before_status`: 签收前状态（已确认/待签收）
- `after_status`: 签收后状态（已签收/签收拒绝）
- `cert_check_result`: 证书核对结果（1通过/2不通过）
- `oil_check_result`: 油品核对结果（1通过/2不通过）
- `window_check_result`: 窗口核对结果（1通过/2不通过）

---

### 验收路径1：成功签收

**场景**: APPLY005 已确认，油品合格(BATCH001硫含量0.35%)，靠泊无冲突

**步骤**:

1. **查询申请详情，确认状态为已确认**
   ```
   GET /api/bunkering/supplier/apply/APPLY005
   ```
   确认: `applyStatus = 5`（已确认）, `sulfurCheckResult = 1`（合格）

2. **供油企业提交签收单（成功）**
   ```json
   POST /api/bunkering/supplier/sign/submit
   Content-Type: application/json

   {
     "applyId": "APPLY005",
     "actualQuantity": 498.50,
     "signTime": "2024-06-10T11:30:00",
     "signer": "张三",
     "remark": "加注作业完成，实际量与计划量略有差异",
     "operator": "supplier001"
   }
   ```
   **成功响应**:
   ```json
   {
     "code": 0,
     "message": "签收单提交成功",
     "data": {
       "id": "SIGNxxxxxx",
       "receiptNo": "SRxxxxxx",
       "applyId": "APPLY005",
       "beforeStatus": 5,
       "afterStatus": 10,
       "certCheckResult": 1,
       "oilCheckResult": 1,
       "windowCheckResult": 1,
       "receiptStatus": 1
     }
   }
   ```

3. **港航监管查询签收核对结果**
   ```
   GET /api/bunkering/authority/sign/check/APPLY005
   ```
   确认:
   - `certCheckResult = 1`，证书均在有效期内
   - `sulfurCheckResult.compliant = true`，硫含量0.35%合格
   - `windowCheckResult.conflict = false`，无靠泊冲突
   - `applyStatus = 10`（已签收）

4. **查看签收历史记录**
   ```
   GET /api/bunkering/authority/sign/history/APPLY005
   ```
   确认: 包含完整的状态变化记录

---

### 验收路径2：硫含量超标导致签收拒绝

**场景**: APPLY006 已确认，但油品BATCH003硫含量0.85%超标

**步骤**:

1. **确认油品硫含量超标**
   ```
   GET /api/bunkering/authority/oil-batch/check-sulfur/BATCH003
   ```
   确认: `compliant = false`，硫含量0.85% > 0.5%

2. **供油企业提交签收单（硫含量超标拒绝）**
   ```json
   POST /api/bunkering/supplier/sign/submit
   Content-Type: application/json

   {
     "applyId": "APPLY006",
     "actualQuantity": 300.00,
     "signTime": "2024-06-10T11:00:00",
     "signer": "李四",
     "remark": "加注完成，等待审核",
     "operator": "supplier002"
   }
   ```
   **拒绝响应**:
   ```json
   {
     "code": 9,
     "message": "油品硫含量超标，签收不通过。油品硫含量超标，拦截加注申请。批次号: FUEL20240603，硫含量: 0.85%，限值: 0.5%，超出: 0.35%，签收单已记录但未通过审核"
   }
   ```

3. **查询申请状态，确认已变更为签收拒绝**
   ```
   GET /api/bunkering/supplier/apply/APPLY006
   ```
   确认: `applyStatus = 11`（签收拒绝）, `rejectReason` 包含硫含量超标信息

4. **港航监管查看核对结果**
   ```
   GET /api/bunkering/authority/sign/check/APPLY006
   ```
   确认:
   - `oilCheckResult = 2`，硫含量超标
   - `sulfurCheckResult.compliant = false`
   - `applyStatus = 11`（签收拒绝）

---

### 验收路径3：靠泊计划冲突导致签收拒绝

**场景**: APPLY007 已确认，但作业时间与靠泊计划PLAN004冲突

**步骤**:

1. **确认靠泊计划冲突**
   - APPLY007计划时间: 2024-06-11 08:00-12:00
   - 靠泊计划PLAN004: 2024-06-11 08:00-12:00，船舶: SHIP001 远洋号

2. **供油企业提交签收单（靠泊冲突拒绝）**
   ```json
   POST /api/bunkering/supplier/sign/submit
   Content-Type: application/json

   {
     "applyId": "APPLY007",
     "actualQuantity": 200.00,
     "signTime": "2024-06-11T10:00:00",
     "signer": "王五",
     "remark": "加注完成",
     "operator": "supplier001"
   }
   ```
   **拒绝响应**:
   ```json
   {
     "code": 9,
     "message": "靠泊计划冲突，签收不通过。作业窗口与靠泊计划冲突。冲突计划: BP20240611001，船舶: 远洋号，作业类型: 加油，时间: 2024-06-11 08:00:00.0 至 2024-06-11 12:00:00.0，签收单已记录但未通过审核"
   }
   ```

3. **查询申请状态，确认已变更为签收拒绝**
   ```
   GET /api/bunkering/supplier/apply/APPLY007
   ```
   确认: `applyStatus = 11`（签收拒绝）, `rejectReason` 包含靠泊冲突信息

4. **查看签收历史，记录完整状态变化**
   ```
   GET /api/bunkering/authority/sign/history/APPLY007
   ```
   确认:
   - `beforeStatus = 5`（已确认）
   - `afterStatus = 11`（签收拒绝）
   - `windowCheckResult = 2`，存在靠泊冲突
   - `windowCheckMsg` 包含冲突详情

---

## 统一响应格式

```json
{
  "code": 0,
  "message": "操作成功",
  "data": {}
}
```

- `code = 0`: 成功
- `code = 9`: 业务错误
- `code = 500`: 系统异常

## 配置说明

`application.yml` 关键配置：

```yaml
bunkering:
  sulfur-limit: 0.5  # 硫含量限值(%)
```

## 数据库设计

### bunkering_apply（加注申请表）核心字段

| 字段 | 类型 | 说明 |
|------|------|-----|
| id | VARCHAR | 主键 |
| ship_code | VARCHAR | 船舶代码 |
| oil_batch_id | VARCHAR | 油品批次ID |
| apply_status | INT | 申请状态 |
| cert_review_status | INT | 证书复核状态 |
| sulfur_check_result | INT | 硫含量检查结果(1合格,2不合格) |
| window_conflict_flag | INT | 窗口冲突标识 |
| reject_reason | VARCHAR | 拒绝原因 |
| plan_start_time | DATETIME | 计划开始时间 |
| plan_end_time | DATETIME | 计划结束时间 |

## 业务规则设计

### SulfurContentValidator（硫含量校验器）

```java
public ValidationResult validate(OilBatch batch) {
    if (batch.getSulfurContent().compareTo(sulfurLimit) > 0) {
        result.setCompliant(false);
        result.setMessage("油品硫含量超标，拦截加注申请。批次号: " + 
            batch.getBatchNo() + "，硫含量: " + batch.getSulfurContent() + 
            "%，限值: " + sulfurLimit + "%");
    }
    return result;
}
```

### CertExpireValidator（证书过期校验器）

检查船舶所有证书是否过期或状态无效，拦截过期船舶的加注申请。

### WorkWindowConflictValidator（作业窗口冲突校验器）

检查计划作业时间是否在可用窗口内，是否与靠泊计划冲突。

## 注意事项

1. 数据中预置的日期为 2024 年，运行测试时请注意系统时间可能导致的证书过期状态变化
2. H2 为内存数据库，重启后数据将重置
3. 硫含量限值可通过 `bunkering.sulfur-limit` 配置调整
4. 逻辑删除字段 `deleted` 由 MyBatis Plus 自动维护

## 许可证

This is a demo project for maritime bunkering compliance management.
