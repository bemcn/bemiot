# BemIot Core 数字基建物联网平台

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/jetlinks/jetlinks-community/maven.yml?branch=master)
![Version](https://img.shields.io/badge/version-1.0-brightgreen)
![Java Version](https://img.shields.io/badge/java-17+-blue)
![SpringBoot Version](https://img.shields.io/badge/springboot-3.2.3-blue)
![Gradle Version](https://img.shields.io/badge/gradle-8.14.2+-blue)
[![LICENSE](https://img.shields.io/badge/license-GPL3.0-success)](https://github.com/bemcn/BemIot/blob/master/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/bemcn/BemIot?style=social)](https://github.com/bemcn/BemIot)

[English](./README.md) | 简体中文

部分软件功能尚未完成优化，存在一定的BUG，正在修复中..

## 项目介绍
BemIot Core 是一个开箱即用，可二次开发的轻量级数字基建物联网平台，它提供完整的物联网接入、管理、分析和应用解决方案。平台基于 Java 语言开发，支持跨平台独立部署或是分布式部署，可快速搭建起包含设备管理、规则联动、告警通知、视频监控、边缘计算等功能的完整 IoT 业务系统。

## 版权声明
BemIot Core 遵循 GPL-3.0 开源协议开放全部源码，用于提供技术交流学习。根据该协议，修改或衍生自 BemIot Core 的代码，不得以闭源的商业软件形式发布或销售。如果您需要将 BemIot Core 在本地用于任何商业目的，请通过平台官网免费加入[合作联盟](#)获取授权，或联系项目负责人进行商业授权，以确保您的使用符合 GPL 协议。

## 核心特性
- **快速部署**：基于 Spring Boot 3.2.3 开发，提供开箱即用的功能，快速搭建物联网系统。
- **开放源码**：全部源代码开放，可按需进行自由拓展。软件前后端分离设计,接口全开放。
- **灵活接入**：支持 TCP、UDP、HTTP、Websocket、MQTT、CoAP、OPC UA、Modbus、SNMP、ICE104、JT808、GB212等多种协议。
- **插件驱动**：采用热插拔插件模式，支持插件开发，可快速扩展各类协议驱动，支持 C/C++、Python、Go、C#、PHP、Ruby、Lua等跨语言开发。
- **规则引擎**：采用可视化规则引擎，通过简单拖拽，实现各业务规则联动控制的快速实现。
- **权限控制**：灵活的非侵入数据权限控制。可实现菜单、按钮、数据三维维度的数据权限控制。可控制单条数据的操作权限。
- **高效存储**：集成 TDengine 时序数据库，支持百万级数据点秒级处理, 提升数据海量级存储与查询效率。

## 技术栈
### 服务端
- **技术选型**：Spring boot、MyBatis、Jwt、PostgreSQL、Redis、TDengine、EMQX、Netty等
- **开发工具**：IntelliJ IDEA
### WEB端
- **技术选型**：ES6、Vue、Vuex、Vue-router、Vue-cli、Axios、Naive UI、Echart等
- **开发工具**：Visual Studio Code

前端源码请移步：https://github.com/bemcn/BemIot-Web 获取。

## 系统要求
### 最低配置
- **操作系统** - Linux、macOS、Windows
- **Java版本** - JDK 17 或更高版本
- **内存** - 4GB（推荐 8GB+）
- **磁盘** - 20GB（推荐 50GB+）
- **网络** - 稳定的网络连接

### 平台演示
尚未开通，在近期计划中。
### 快速开始
文档正在制作中..
### 平台架构
文档正在制作中..
### 核心依赖
| 组件 | 版本          | 用途                          |
|-----|-------------|-----------------------------|
| **PostgreSQL** | 14.x+        | 关系数据库，存储业务数据                |
| **Redis** | 6.0+        | 缓存和消息队列                     |
| **TDengine** | 3.0+        | 时序数据库，用于高效存储设备时序数据      |
| **EMQX** | 5.8.2+         | 消息中间件（可使用其他中间件或云服务）     |
| **Zlmediakit** | -           | 流媒体服务框架,监控视频推拉流 |
| **Nginx** | ≥ 1.27.2        | Web服务器             |

## 常见应用场景
### 工业生产监控
- 工厂设备实时监控、生产数据采集
- 设备运维告警、故障预警和处理
- 生产效率分析和优化

### 智慧楼宇
- 建筑设备（HVAC、照明、安防）联动控制
- 能耗管理和优化
- 环境监测（温湿度、空气质量等）

### 环境监测
- 实时环境数据采集和监测
- 污染物预警和告警
- 环保数据上报和合规性验证

### 智慧农业
- 田间传感器部署和数据采集
- 环境监测（温湿度、光照、土壤含水量）
- 自动化灌溉和施肥控制

### 新能源管理
- 光伏、风电、储能设备监控
- 发电量实时统计和分析
- 电池管理系统（BMS）集成

### 车联网平台
- 车辆设备接入和管理
- 车辆位置追踪和路线规划
- 故障预警和维护提醒


### 贡献代码
我们欢迎各种形式的贡献！无论是代码、文档、Bug 报告还是功能建议，都能帮助我们改进项目。
详情请访问下方的文档链接。
- [贡献者指南>>](./Contributions.md)
- [Git提交规范>>](./GitSubmit.md)
- [功能规划>>](./RoadMap.md)

## 📋 常见问题 (FAQ)

### Q: BemIot Core开源版本是否可免费使用？
**A:** BemIot Core 遵循 GPL-3.0 开源协议，个人学习和使用完全免费。如需商业授权，请通过平台官网免费加入[合作联盟](#)获取授权，或联系项目负责人进行商业授权。

### Q: 部署环境支持哪些？
**A:** BemIot Core是一个跨平台的软件，能够在Windows、linux、unix和mac系统上部署。除了私有化部署外，还支持 AWS、阿里云、腾讯云、华为云等云平台部署。在后续版本中将陆续推出 Docker 和 Kubernetes的部署方式。

### Q: 支持哪些设备接入？
**A:** BemIot Core 支持任何支持 TCP、MQTT、UDP、CoAP、HTTP、Websocket 等协议的设备接入，也可通过插件扩展支持更多协议。在后续版本中，将不断更新协议驱动。

### Q: 数据量很大，性能能跟上吗？
**A:** BemIot Core 集成 TDengine 时序数据库，可支持百万级数据点秒级处理。同时支持数据分片、缓存优化等性能优化方案。

### Q: 如何实现设备远程控制？
**A:** 通过设备管理模块中的设备命令功能，可实现远程控制。支持实时控制、定时控制、单一设备控制、批量设备群控、场景化规则自动控制。

### Q: 是否支持私有部署？
**A:** 完全支持私有部署。您可以在自己的服务器或内网上部署 BemIot Core，数据完全属于您。

### Q: 如何实现边缘计算？
**A:** BemIot Core 支持离线部署和本地规则执行，可在边缘设备上运行，实现边缘计算和离线自治。

### Q: 能否集成第三方系统？
**A:** 支持 OpenAPI 集成，后续版本将逐步与各大云平台IOT服务对接，轻松集成第三方系统。

## 免责声明

BemIot Core开源版本是一个开源学习项目，与商业行为无关。用户在使用该项目时，应遵循法律法规，不得进行非法活动。

- 如果 发现用户有违法行为，将会配合相关机关进行调查并向政府部门举报
- 用户因非法行为造成的任何法律责任均由用户自行承担
- 如因用户使用造成第三方损害的，用户应当依法予以赔偿
- 使用BemIot Core所有相关资源均由用户自行承担风险
- 本项目不提供任何担保或保证

## 开源协议
本项目采用 [GPL-3.0](./LICENSE) 开源协议。详见 [LICENSE](./LICENSE) 文件。

