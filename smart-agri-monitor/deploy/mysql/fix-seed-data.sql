SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_agri;

UPDATE role SET role_name = '系统管理员' WHERE id = 1;
UPDATE user SET display_name = '管理员' WHERE id = 1;

UPDATE farm_plot
SET name = '一号温室',
    location = '东区 A-01',
    crop_type = '番茄',
    manager = '张工'
WHERE id = 1;

UPDATE farm_plot
SET name = '二号露天地块',
    location = '西区 B-03',
    crop_type = '玉米',
    manager = '李工'
WHERE id = 2;

UPDATE device SET device_name = '一号温室综合传感器' WHERE id = 1;
UPDATE device SET device_name = '二号地块综合传感器' WHERE id = 2;

UPDATE alarm_rule SET rule_name = '温度过高预警' WHERE metric_type = 'temperature';
UPDATE alarm_rule SET rule_name = '土壤湿度过低预警' WHERE metric_type = 'soil_moisture';
UPDATE alarm_rule SET rule_name = '光照异常预警' WHERE metric_type = 'light';

UPDATE alarm_record ar
JOIN alarm_rule r ON r.id = ar.rule_id
SET ar.message = CONCAT(r.rule_name, ' 触发: ', ar.metric_type, '=', ar.metric_value);
