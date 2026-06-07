-- 船舶证书表
CREATE TABLE IF NOT EXISTS ship_cert (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    ship_code VARCHAR(50) NOT NULL COMMENT '船舶编号',
    ship_name VARCHAR(100) NOT NULL COMMENT '船舶名称',
    cert_type VARCHAR(50) NOT NULL COMMENT '证书类型',
    cert_name VARCHAR(100) COMMENT '证书名称',
    cert_no VARCHAR(50) NOT NULL COMMENT '证书编号',
    issue_date DATE NOT NULL COMMENT '签发日期',
    expiry_date DATE NOT NULL COMMENT '有效期至',
    issue_authority VARCHAR(100) COMMENT '签发机构',
    issuer VARCHAR(100) COMMENT '签发人',
    cert_status INT DEFAULT 1 COMMENT '证书状态 1-有效 0-无效',
    remark VARCHAR(500) COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    UNIQUE KEY uk_ship_cert (ship_code, cert_type, cert_no)
);

-- 油品批次表
CREATE TABLE IF NOT EXISTS oil_batch (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    batch_no VARCHAR(50) NOT NULL UNIQUE COMMENT '批次号',
    oil_type VARCHAR(50) NOT NULL COMMENT '油品类型',
    oil_name VARCHAR(100) COMMENT '油品名称',
    sulfur_content DECIMAL(10,4) NOT NULL COMMENT '硫含量(%)',
    viscosity DECIMAL(10,2) COMMENT '粘度',
    density DECIMAL(10,4) COMMENT '密度',
    batch_status INT DEFAULT 1 COMMENT '批次状态 1-合格 2-不合格',
    supplier_code VARCHAR(50) COMMENT '供应商编码',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    production_date DATE COMMENT '生产日期',
    arrival_date DATE COMMENT '到港日期',
    storage_tank VARCHAR(50) COMMENT '存储罐号',
    total_quantity DECIMAL(12,2) COMMENT '总数量(吨)',
    available_quantity DECIMAL(12,2) COMMENT '可用数量(吨)',
    quality_report_no VARCHAR(50) COMMENT '质量报告编号',
    inspection_report_no VARCHAR(50) COMMENT '检验报告编号',
    supplier VARCHAR(100) COMMENT '供应商',
    remark VARCHAR(500) COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
);

-- 作业窗口表
CREATE TABLE IF NOT EXISTS work_window (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    window_code VARCHAR(50) NOT NULL UNIQUE COMMENT '窗口编号',
    window_name VARCHAR(100) NOT NULL COMMENT '窗口名称',
    berth_code VARCHAR(50) NOT NULL COMMENT '泊位编号',
    berth_name VARCHAR(100) COMMENT '泊位名称',
    window_date DATE NOT NULL COMMENT '窗口日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    window_status INT DEFAULT 1 COMMENT '窗口状态 1-可用 2-占用 3-关闭',
    max_vessels INT DEFAULT 1 COMMENT '最大作业船舶数',
    max_quantity INT COMMENT '最大作业量',
    remark VARCHAR(500) COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    UNIQUE KEY uk_window (berth_code, window_date, start_time, end_time)
);

-- 靠泊计划表
CREATE TABLE IF NOT EXISTS berth_plan (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    plan_no VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编号',
    berth_code VARCHAR(50) NOT NULL COMMENT '泊位编号',
    berth_name VARCHAR(100) COMMENT '泊位名称',
    ship_code VARCHAR(50) NOT NULL COMMENT '船舶编号',
    ship_name VARCHAR(100) NOT NULL COMMENT '船舶名称',
    plan_berth_date DATE NOT NULL COMMENT '计划靠泊日期',
    plan_start_time TIMESTAMP NOT NULL COMMENT '计划开始时间',
    plan_end_time TIMESTAMP NOT NULL COMMENT '计划结束时间',
    plan_status INT DEFAULT 1 COMMENT '计划状态 1-待确认 2-已确认 3-已取消 4-已完成',
    operation_type VARCHAR(50) NOT NULL COMMENT '作业类型',
    remark VARCHAR(500) COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
);

-- 加注申请表
CREATE TABLE IF NOT EXISTS bunkering_apply (
    id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    apply_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    ship_code VARCHAR(50) NOT NULL COMMENT '船舶编号',
    ship_name VARCHAR(100) NOT NULL COMMENT '船舶名称',
    agent_code VARCHAR(50) NOT NULL COMMENT '船舶代理编号',
    agent_name VARCHAR(100) NOT NULL COMMENT '船舶代理名称',
    supplier_code VARCHAR(50) COMMENT '供油企业编号',
    supplier_name VARCHAR(100) COMMENT '供油企业名称',
    oil_batch_id VARCHAR(36) COMMENT '油品批次ID',
    oil_type VARCHAR(50) NOT NULL COMMENT '油品类型',
    oil_quantity DECIMAL(12,2) NOT NULL COMMENT '加油数量(吨)',
    berth_code VARCHAR(50) NOT NULL COMMENT '泊位编号',
    work_window_id VARCHAR(36) COMMENT '作业窗口ID',
    plan_start_time TIMESTAMP NOT NULL COMMENT '计划开始时间',
    plan_end_time TIMESTAMP NOT NULL COMMENT '计划结束时间',
    cert_review_status INT DEFAULT 0 COMMENT '证书复核状态 0-待复核 1-已复核 2-复核不通过',
    cert_review_comment VARCHAR(500) COMMENT '证书复核意见',
    cert_review_time TIMESTAMP COMMENT '证书复核时间',
    cert_reviewer VARCHAR(50) COMMENT '证书复核人',
    apply_status INT DEFAULT 0 COMMENT '申请状态 0-草稿 1-已提交 2-证书复核通过 3-油品检测通过 4-待确认 5-已确认 6-已拒绝 7-已完成 8-已取消',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    confirm_time TIMESTAMP COMMENT '确认时间',
    sulfur_check_result INT DEFAULT 0 COMMENT '硫含量检查结果 0-未检查 1-合格 2-超标',
    window_conflict_flag INT DEFAULT 0 COMMENT '窗口冲突标识 0-无冲突 1-有冲突',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
);

CREATE INDEX idx_apply_ship ON bunkering_apply(ship_code);
CREATE INDEX idx_apply_status ON bunkering_apply(apply_status);
CREATE INDEX idx_apply_supplier ON bunkering_apply(supplier_code);
CREATE INDEX idx_apply_cert_status ON bunkering_apply(cert_review_status);
