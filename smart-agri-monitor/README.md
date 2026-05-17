# 智慧农业设备监测与预警平台

面向 Java 后端岗面试的中等 MVP：农田地块接入模拟传感器，系统展示实时环境数据，按规则触发告警，并支持告警处理、历史查询、统计看板和 Excel 导出。

## 技术栈

- 后端：Spring Boot 3、MyBatis-Plus、MySQL 8、Redis、JWT、EasyExcel、Springdoc OpenAPI
- 前端：Vue 3、Vite、Element Plus、ECharts
- 部署：Docker Compose

## 快速启动

```powershell
cd C:\Users\yyds\GitHub\ai-works\smart-agri-monitor
docker compose up --build
```

访问：

- 前端：http://localhost:8088
- 后端健康检查：http://localhost:8080/api/health
- Swagger：http://localhost:8080/swagger-ui.html

默认账号：

- 用户名：`admin`
- 密码：`admin123`

## 核心流程

1. 登录后台。
2. 查看首页看板。
3. 管理地块、设备和预警规则。
4. 后端定时任务每 10 秒生成温度、空气湿度、土壤湿度、光照数据。
5. 数据入库后匹配预警规则，自动生成告警。
6. 在告警处理页把告警改为处理中或已处理。
7. 在历史数据页查看曲线，在报表页导出 Excel。

## 项目结构

```text
smart-agri-monitor
├─ backend               Spring Boot 后端
├─ frontend              Vue 3 管理后台
├─ deploy/mysql/init.sql 数据库表结构和种子数据
├─ docs/architecture.md  架构图和面试讲解点
└─ docker-compose.yml    一键部署 MySQL、Redis、后端、前端
```

## 重点接口

- `POST /api/auth/login` 登录
- `GET /api/dashboard/stats` 看板统计
- `GET /api/plots` 地块分页
- `GET /api/devices` 设备分页
- `GET /api/rules` 预警规则分页
- `POST /api/sensor-data` 设备数据上报
- `GET /api/sensor-data/latest` 最新数据
- `GET /api/sensor-data/history` 历史数据
- `PUT /api/alarms/{id}/handle` 告警处理
- `GET /api/reports/sensor-data` 传感器数据导出
- `GET /api/reports/alarms` 告警记录导出

## 简历描述

基于 Spring Boot + MyBatis-Plus + MySQL + Redis + Vue 3 设计并实现智慧农业设备监测与预警平台。项目包含 JWT 登录鉴权、地块与设备管理、模拟传感器数据采集、规则阈值预警、告警去重与处理、ECharts 看板、历史查询和 EasyExcel 报表导出，并使用 Docker Compose 完成一键部署。
