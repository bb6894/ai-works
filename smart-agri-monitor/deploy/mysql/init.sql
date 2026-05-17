SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS smart_agri DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_agri;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS alarm_record;
DROP TABLE IF EXISTS alarm_rule;
DROP TABLE IF EXISTS sensor_data;
DROP TABLE IF EXISTS device;
DROP TABLE IF EXISTS farm_plot;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;

CREATE TABLE role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL UNIQUE,
  role_name VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(128) NOT NULL,
  display_name VARCHAR(64) NOT NULL,
  role_id BIGINT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_role (role_id)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE farm_plot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  location VARCHAR(255) NOT NULL,
  area_mu DECIMAL(10,2) NOT NULL DEFAULT 0,
  crop_type VARCHAR(64) NOT NULL,
  manager VARCHAR(64) NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE device (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  device_code VARCHAR(64) NOT NULL UNIQUE,
  device_name VARCHAR(128) NOT NULL,
  plot_id BIGINT NOT NULL,
  device_type VARCHAR(64) NOT NULL,
  online_status TINYINT NOT NULL DEFAULT 1,
  installed_at DATE NULL,
  last_seen_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_device_plot (plot_id)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sensor_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  device_id BIGINT NOT NULL,
  plot_id BIGINT NOT NULL,
  metric_type VARCHAR(64) NOT NULL,
  metric_value DECIMAL(12,2) NOT NULL,
  unit VARCHAR(32) NOT NULL,
  collected_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_sensor_device_metric_time (device_id, metric_type, collected_at),
  INDEX idx_sensor_plot_time (plot_id, collected_at)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alarm_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  rule_name VARCHAR(128) NOT NULL,
  plot_id BIGINT NULL,
  device_id BIGINT NULL,
  metric_type VARCHAR(64) NOT NULL,
  compare_operator VARCHAR(16) NOT NULL,
  threshold_value DECIMAL(12,2) NOT NULL,
  threshold_value2 DECIMAL(12,2) NULL,
  alarm_level VARCHAR(32) NOT NULL DEFAULT 'warning',
  enabled TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_rule_enabled_metric (enabled, metric_type)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alarm_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  rule_id BIGINT NOT NULL,
  device_id BIGINT NOT NULL,
  plot_id BIGINT NOT NULL,
  metric_type VARCHAR(64) NOT NULL,
  metric_value DECIMAL(12,2) NOT NULL,
  alarm_level VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  message VARCHAR(255) NOT NULL,
  handler VARCHAR(64) NULL,
  handle_remark VARCHAR(255) NULL,
  handled_at DATETIME NULL,
  triggered_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_alarm_status (status),
  INDEX idx_alarm_rule_device_status (rule_id, device_id, status)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  username VARCHAR(64) NULL,
  module_name VARCHAR(64) NOT NULL,
  action_name VARCHAR(64) NOT NULL,
  request_method VARCHAR(16) NULL,
  request_uri VARCHAR(255) NULL,
  ip_address VARCHAR(64) NULL,
  success TINYINT NOT NULL DEFAULT 1,
  error_message VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_operation_user_time (user_id, created_at)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO role (id, role_code, role_name) VALUES
(1, 'ADMIN', '系统管理员');

INSERT INTO user (id, username, password_hash, display_name, role_id, status) VALUES
(1, 'admin', '{bcrypt-on-startup}', '管理员', 1, 1);

INSERT INTO farm_plot (id, name, location, area_mu, crop_type, manager, status) VALUES
(1, '一号温室', '东区 A-01', 18.50, '番茄', '张工', 1),
(2, '二号露天地块', '西区 B-03', 42.00, '玉米', '李工', 1);

INSERT INTO device (id, device_code, device_name, plot_id, device_type, online_status, installed_at, last_seen_at) VALUES
(1, 'DEV-TOMATO-001', '一号温室综合传感器', 1, 'multi_sensor', 1, '2026-05-01', NOW()),
(2, 'DEV-CORN-001', '二号地块综合传感器', 2, 'multi_sensor', 1, '2026-05-02', NOW());

INSERT INTO alarm_rule (rule_name, plot_id, device_id, metric_type, compare_operator, threshold_value, threshold_value2, alarm_level, enabled) VALUES
('温度过高预警', NULL, NULL, 'temperature', 'GT', 30.00, NULL, 'critical', 1),
('土壤湿度过低预警', NULL, NULL, 'soil_moisture', 'LT', 35.00, NULL, 'warning', 1),
('光照异常预警', NULL, NULL, 'light', 'OUTSIDE', 5000.00, 70000.00, 'warning', 1);
