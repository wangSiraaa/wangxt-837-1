-- 船舶证书数据
INSERT INTO ship_cert (id, ship_code, ship_name, cert_type, cert_name, cert_no, issue_date, expiry_date, issue_authority, issuer, cert_status, remark, create_by, update_by) VALUES
('CERT001', 'SHIP001', '远洋号', '船舶国籍证书', '船舶国籍证书', 'GZ2023001', '2023-01-15', '2028-01-14', '中国海事局', '张三', 1, '正常有效', 'admin', 'admin'),
('CERT002', 'SHIP001', '远洋号', '船舶检验证书', '船舶检验证书', 'JY2023001', '2023-03-20', '2025-06-19', '中国船级社', '李四', 1, '正常有效', 'admin', 'admin'),
('CERT003', 'SHIP001', '远洋号', '最低安全配员证书', '最低安全配员证书', 'PY2023001', '2023-02-10', '2026-02-09', '中国海事局', '王五', 1, '正常有效', 'admin', 'admin'),
('CERT004', 'SHIP002', '江河号', '船舶国籍证书', '船舶国籍证书', 'GZ2022001', '2022-05-10', '2027-05-09', '中国海事局', '赵六', 1, '正常有效', 'admin', 'admin'),
('CERT005', 'SHIP002', '江河号', '船舶检验证书', '船舶检验证书', 'JY2022001', '2022-06-15', '2024-03-14', '中国船级社', '钱七', 0, '已过期，用于验收测试', 'admin', 'admin'),
('CERT006', 'SHIP003', '致远号', '船舶国籍证书', '船舶国籍证书', 'GZ2024001', '2024-01-10', '2029-01-09', '中国海事局', '孙八', 1, '正常有效', 'admin', 'admin'),
('CERT007', 'SHIP003', '致远号', '船舶检验证书', '船舶检验证书', 'JY2024001', '2024-02-20', '2026-05-19', '中国船级社', '周九', 1, '正常有效', 'admin', 'admin');

-- 油品批次数据（包含一个超标油品用于验收测试）
INSERT INTO oil_batch (id, batch_no, oil_type, oil_name, sulfur_content, viscosity, density, batch_status, supplier_code, supplier_name, production_date, arrival_date, storage_tank, total_quantity, available_quantity, quality_report_no, inspection_report_no, supplier, remark, create_by, update_by) VALUES
('BATCH001', 'FUEL20240601', '燃料油', '低硫燃料油', 0.35, 380.00, 0.9850, 1, 'SUPP001', '中石化燃料油销售有限公司', '2024-05-15', '2024-05-20', 'TANK-A1', 5000.00, 4500.00, 'QR20240515001', 'INS20240515001', '中石化燃料油销售有限公司', '硫含量0.35%，符合标准', 'admin', 'admin'),
('BATCH002', 'FUEL20240602', '柴油', '车用柴油', 0.10, 4.50, 0.8400, 1, 'SUPP001', '中石油燃料油有限公司', '2024-05-20', '2024-05-25', 'TANK-B2', 3000.00, 2800.00, 'QR20240520001', 'INS20240520001', '中石油燃料油有限公司', '硫含量0.10%，符合标准', 'admin', 'admin'),
('BATCH003', 'FUEL20240603', '燃料油', '高硫燃料油', 0.85, 420.00, 0.9920, 2, 'SUPP002', '某地方炼油厂', '2024-05-25', '2024-05-30', 'TANK-C3', 2000.00, 2000.00, 'QR20240525001', 'INS20240525001', '某地方炼油厂', '硫含量0.85%，超标不合格，用于验收测试', 'admin', 'admin'),
('BATCH004', 'FUEL20240604', '燃料油', '低硫燃料油', 0.45, 390.00, 0.9870, 1, 'SUPP001', '中石化燃料油销售有限公司', '2024-06-01', '2024-06-05', 'TANK-A2', 4000.00, 3800.00, 'QR20240601001', 'INS20240601001', '中石化燃料油销售有限公司', '硫含量0.45%，符合标准', 'admin', 'admin'),
('BATCH005', 'FUEL20240605', '柴油', '车用柴油', 0.08, 4.20, 0.8350, 1, 'SUPP001', '中石油燃料油有限公司', '2024-06-02', '2024-06-06', 'TANK-B3', 2500.00, 2400.00, 'QR20240602001', 'INS20240602001', '中石油燃料油有限公司', '硫含量0.08%，符合标准', 'admin', 'admin');

-- 作业窗口数据
INSERT INTO work_window (id, window_code, window_name, berth_code, berth_name, window_date, start_time, end_time, window_status, max_vessels, max_quantity, remark, create_by, update_by) VALUES
('WIN001', 'BERTH1-20240610-AM', '1号泊位6月10日上午', 'BERTH001', '1号泊位', '2024-06-10', '08:00:00', '12:00:00', 1, 1, 500, '正常作业窗口', 'admin', 'admin'),
('WIN002', 'BERTH1-20240610-PM', '1号泊位6月10日下午', 'BERTH001', '1号泊位', '2024-06-10', '13:00:00', '17:00:00', 1, 1, 500, '正常作业窗口', 'admin', 'admin'),
('WIN003', 'BERTH1-20240610-NG', '1号泊位6月10日夜间', 'BERTH001', '1号泊位', '2024-06-10', '19:00:00', '23:00:00', 1, 1, 300, '夜间作业窗口', 'admin', 'admin'),
('WIN004', 'BERTH2-20240610-AM', '2号泊位6月10日上午', 'BERTH002', '2号泊位', '2024-06-10', '08:00:00', '12:00:00', 1, 2, 800, '可同时作业2艘', 'admin', 'admin'),
('WIN005', 'BERTH2-20240610-PM', '2号泊位6月10日下午', 'BERTH002', '2号泊位', '2024-06-10', '13:00:00', '17:00:00', 1, 2, 800, '可同时作业2艘', 'admin', 'admin'),
('WIN006', 'BERTH1-20240611-AM', '1号泊位6月11日上午', 'BERTH001', '1号泊位', '2024-06-11', '08:00:00', '12:00:00', 1, 1, 500, '正常作业窗口', 'admin', 'admin'),
('WIN007', 'BERTH1-20240611-PM', '1号泊位6月11日下午', 'BERTH001', '1号泊位', '2024-06-11', '13:00:00', '17:00:00', 2, 1, 500, '已占用', 'admin', 'admin');

-- 靠泊计划数据
INSERT INTO berth_plan (id, plan_no, berth_code, berth_name, ship_code, ship_name, plan_berth_date, plan_start_time, plan_end_time, plan_status, operation_type, remark, create_by, update_by) VALUES
('PLAN001', 'BP20240610001', 'BERTH001', '1号泊位', 'SHIP001', '远洋号', '2024-06-10', '2024-06-10 08:00:00', '2024-06-10 12:00:00', 2, '卸货', '已确认靠泊计划', 'admin', 'admin'),
('PLAN002', 'BP20240610002', 'BERTH001', '1号泊位', 'SHIP002', '江河号', '2024-06-10', '2024-06-10 13:00:00', '2024-06-10 17:00:00', 2, '装货', '已确认靠泊计划', 'admin', 'admin'),
('PLAN003', 'BP20240610003', 'BERTH002', '2号泊位', 'SHIP003', '致远号', '2024-06-10', '2024-06-10 09:00:00', '2024-06-10 11:00:00', 1, '加油', '待确认靠泊计划', 'admin', 'admin'),
('PLAN004', 'BP20240611001', 'BERTH001', '1号泊位', 'SHIP001', '远洋号', '2024-06-11', '2024-06-11 08:00:00', '2024-06-11 12:00:00', 1, '加油', '待确认靠泊计划', 'admin', 'admin');

-- 加注申请示例数据
INSERT INTO bunkering_apply (id, apply_no, ship_code, ship_name, agent_code, agent_name, supplier_code, supplier_name, oil_batch_id, oil_type, oil_quantity, berth_code, work_window_id, plan_start_time, plan_end_time, cert_review_status, cert_review_comment, cert_review_time, cert_reviewer, apply_status, reject_reason, confirm_time, sulfur_check_result, window_conflict_flag, create_by, update_by) VALUES
('APPLY001', 'BA202406001', 'SHIP001', '远洋号', 'AGENT001', '上海外轮代理有限公司', 'SUPP001', '中石化燃料油销售有限公司', 'BATCH001', '燃料油', 500.00, 'BERTH001', 'WIN001', '2024-06-10 08:00:00', '2024-06-10 12:00:00', 1, '证书有效，复核通过', '2024-06-09 10:00:00', 'reviewer001', 5, NULL, '2024-06-09 14:00:00', 1, 0, 'admin', 'admin'),
('APPLY002', 'BA202406002', 'SHIP002', '江河号', 'AGENT001', '上海外轮代理有限公司', 'SUPP001', '中石化燃料油销售有限公司', 'BATCH002', '柴油', 200.00, 'BERTH001', 'WIN002', '2024-06-10 13:00:00', '2024-06-10 17:00:00', 2, '船舶检验证书已过期', '2024-06-09 11:00:00', 'reviewer001', 6, '船舶检验证书已过期，证书有效期至2024-03-14', NULL, 1, 0, 'admin', 'admin');
