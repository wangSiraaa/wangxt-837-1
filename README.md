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
│   ├── OilSupplierController.java          # 供油企业接口
│   ├── PortAuthorityController.java        # 港航监管接口
│   └── BaseDataController.java             # 基础数据接口
├── dto/                                     # 数据传输对象
│   ├── BunkeringApplySubmitDTO.java        # 加注申请提交参数
│   └── CertReviewDTO.java                  # 证书复核参数
├── entity/                                  # 实体类
│   ├── ShipCert.java                       # 船舶证书
│   ├── OilBatch.java                       # 油品批次
│   ├── WorkWindow.java                     # 作业窗口
│   ├── BerthPlan.java                      # 靠泊计划
│   └── BunkeringApply.java                 # 加注申请
├── enums/                                   # 枚举类
│   ├── ApplyStatusEnum.java                # 申请状态枚举
│   └── CertReviewStatusEnum.java           # 证书复核状态枚举
├── mapper/                                  # 数据访问层
│   ├── ShipCertMapper.java
│   ├── OilBatchMapper.java
│   ├── WorkWindowMapper.java
│   ├── BerthPlanMapper.java
│   └── BunkeringApplyMapper.java
├── rule/                                    # 业务规则校验器
│   ├── CertExpireValidator.java            # 证书过期校验
│   ├── SulfurContentValidator.java         # 硫含量超标校验
│   └── WorkWindowConflictValidator.java    # 作业窗口冲突校验
└── service/                                 # 业务逻辑层
    ├── BunkeringApplyService.java          # 加注申请服务
    ├── OilBatchService.java                # 油品批次服务
    ├── ShipCertService.java                # 船舶证书服务
    ├── WorkWindowService.java              # 作业窗口服务
    └── BerthPlanService.java               # 靠泊计划服务
```

## 状态机

```
草稿(0) → 已提交(1) → 证书复核通过(2) → 油品检测通过(3) → 待确认(4) → 已确认(5)
                  ↓               ↓                ↓                ↓
               已拒绝(6)       已拒绝(6)        已拒绝(6)        已拒绝(6)
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

### 港航监管接口 (`/api/bunkering/authority`)

| 方法 | 路径 | 描述 |
|------|------|-----|
| GET | `/oil-batch/{id}` | 查询油品批次 |
| GET | `/oil-batch/batchNo/{batchNo}` | 按批次号查询 |
| GET | `/oil-batch/list` | 查询油品批次列表 |
| GET | `/oil-batch/non-compliant` | 查询不合格油品 |
| GET | `/oil-batch/check-sulfur/{id}` | 检查硫含量 |

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
