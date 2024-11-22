# 🚀 SpringBoot 项目初始模板

> 作者：bruce

## 📋 项目简介

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

- ⚡️ 1分钟即可完成内容网站后端搭建
- 🔨 可在此基础上快速开发自己的项目
- 💡 提供完整的示例代码与注释

## ✨ 核心特性

### 🏗️ 技术架构

| 类别   | 技术栈                      |
|------|--------------------------|
| 核心框架 | Spring Boot 2.7.x        |
| 数据存储 | MySQL、Redis |
| 对象存储 | 腾讯云 COS                  |
| 安全框架 | Sa-Token                 |
| 工具库  | Hutool、Lombok、Easy Excel |

### 🛠️ 主要功能

#### 系统功能

- ✅ 分布式 Session 登录
- ✅ 全局异常处理
- ✅ 自定义权限注解
- ✅ 统一响应封装
- ✅ 全局跨域配置
- ✅ 多环境配置

#### 业务功能

- 👤 用户管理
    - 登录注册
    - 权限控制
    - 信息修改

## 🚦 快速开始

### 1. 配置数据库

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

### 2. 配置 Redis

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 123456
```

### 3. 配置 Elasticsearch

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

### 4. 初始化数据

1. 执行 `sql/create_table.sql` 创建数据表

### 5. 启动服务

启动后访问 Swagger 文档：http://localhost:8101/api/doc.html

## 🧪 单元测试

- 基于 JUnit 5
- 提供完整示例

## 📚 开发建议

1. 查找 `todo` 注释，按需修改配置
2. 遵循项目分层架构
3. 参考示例代码进行功能开发

## 🔔 注意事项

- 请确保 MySQL、Redis、Elasticsearch 服务已启动
- 注意修改各项配置为实际环境信息
- 建议先阅读示例代码再开发新功能

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request 一起改进项目！

## 📄 开源协议

MIT License
