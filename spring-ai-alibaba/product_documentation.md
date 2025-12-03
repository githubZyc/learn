# Spring AI Alibaba 小学智能辅导系统

## 项目概述

这是一个基于Spring Boot和阿里巴巴AI服务的小学智能辅导系统，专为1-6年级学生设计。系统能够提供知识点解释、示例展示和练习题生成等功能，支持文本问答和图片识别分析。

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.3.5
- **Java版本**: Java 21
- **AI服务**: 阿里巴巴DashScope (通义千问)
- **数据库**: MySQL 8.0.33
- **ORM**: Spring Data JPA
- **文件处理**: Apache POI (文档解析)
- **缓存**: Redis (会话管理和热点数据缓存)
- **消息队列**: RabbitMQ/Kafka (异步任务处理)
- **监控**: Micrometer + Prometheus
- **日志**: SLF4J + Logback

### 前端技术栈
- **核心**: HTML5 + JavaScript (原生实现)
- **样式**: CSS3
- **特性**: 响应式设计，支持移动端
- **状态管理**: Redux/Vuex (复杂应用)
- **构建工具**: Webpack/Vite

## 核心功能模块

### 1. 智能问答系统
系统提供三种不同的教学方法：

#### 解释模式 (explain)
- 详细解释学生提出的知识点
- 使用简单易懂的语言，适合小学生理解
- 支持图文结合的解释方式

#### 示例模式 (example)
- 提供与问题相关的具体示例
- 帮助学生通过实例更好地掌握知识点
- 支持图文结合的示例展示

#### 练习模式 (exercise)
- 生成与知识点相关的练习题
- 提供详细的解题过程和答案
- 支持图文结合的题目展示

### 2. 图片识别问答
- 支持上传图片进行问题分析
- 能够识别图片中的数学题目等内容
- 结合图片内容提供针对性解答

### 3. 对话管理
- 多轮对话支持，保持上下文连贯性
- 会话历史管理，区分不同用户会话
- 系统消息定制，针对不同场景优化
- 对话状态跟踪和恢复
- 对话超时自动清理机制

### 4. 图像生成功能
- 根据问题内容自动生成辅助教学图像
- 使用阿里云万相模型进行图像合成
- 在回答中嵌入图像链接以增强理解
- 图像缓存和CDN加速

### 5. 文档管理
- 支持文档存储和检索
- 提供基于关键词的内容搜索
- 记录文档创建和更新时间
- 文档版本控制
- 文档权限管理

### 6. 用户管理系统
- 用户注册/登录/认证
- 角色权限管理（学生、教师、管理员）
- 学习进度跟踪
- 个人资料管理

### 7. 学习分析系统
- 学习行为数据分析
- 知识点掌握度评估
- 个性化推荐
- 学习报告生成

### 8. 系统管理
- 配置管理
- 日志管理
- 监控告警
- 数据备份与恢复
- 系统健康检查

## API接口

### 问答接口
```
POST /api/question/ask
功能: 基本文本问答
参数: question (问题内容), sessionId (会话ID)

POST /api/question/ask-with-image
功能: 图片问答
参数: question (问题内容), image (图片文件), sessionId (会话ID)

POST /api/question/ask-with-method
功能: 指定教学方法问答
参数: question (问题内容), method (教学方法), sessionId (会话ID)
```

### 用户管理接口
```
POST /api/auth/register
功能: 用户注册
参数: username, password, email

POST /api/auth/login
功能: 用户登录
参数: username, password

GET /api/user/profile
功能: 获取用户信息
参数: userId

PUT /api/user/profile
功能: 更新用户信息
参数: profileData
```

### 学习分析接口
```
GET /api/learning/progress
功能: 获取学习进度
参数: userId

GET /api/learning/report
功能: 获取学习报告
参数: userId, dateRange

GET /api/learning/recommendations
功能: 获取学习推荐
参数: userId
```

### 系统管理接口
```
GET /api/admin/config
功能: 获取系统配置
参数: adminToken

POST /api/admin/config
功能: 更新系统配置
参数: configData, adminToken

GET /api/admin/logs
功能: 获取系统日志
参数: adminToken, dateRange
```

### WebSocket实时通信
```
WS /api/ws/chat
功能: 实时聊天通信
参数: sessionId, userId
```

## 前端界面

### 主要组件
1. **聊天界面**: 显示用户和AI的对话记录
2. **输入区域**: 问题输入框和功能按钮
3. **教学方法切换**: 解释、示例、练习三种模式按钮
4. **图片上传**: 支持图片上传和预览
5. **用户中心**: 个人资料、学习进度、历史记录
6. **学习报告**: 可视化学习数据分析
7. **设置面板**: 个性化配置选项

### 用户体验特性
- 实时响应指示器
- 消息气泡设计区分用户和AI角色
- 时间戳显示
- 响应式布局适配不同设备
- 图片预览功能
- 键盘快捷键支持
- 语音输入支持
- 暗色/亮色主题切换
- 多语言支持
- 无障碍访问支持

## 数据库设计

### 用户表 (users)
- id: 主键
- username: 用户名
- password: 密码（加密存储）
- email: 邮箱
- role: 角色（学生/教师/管理员）
- createdAt: 创建时间
- updatedAt: 更新时间
- lastLoginAt: 最后登录时间

### 文档表 (documents)
- id: 主键
- fileName: 文件名
- fileType: 文件类型
- content: 文件内容(大文本)
- createdAt: 创建时间
- updatedAt: 更新时间
- createdBy: 创建者ID
- version: 版本号

### 会话表 (conversations)
- id: 主键
- sessionId: 会话ID
- userId: 用户ID
- title: 会话标题
- createdAt: 创建时间
- updatedAt: 更新时间
- isActive: 是否活跃

### 消息表 (messages)
- id: 主键
- conversationId: 会话ID
- senderType: 发送者类型（user/assistant/system）
- content: 消息内容
- timestamp: 时间戳
- messageType: 消息类型（text/image/link）

### 学习记录表 (learning_records)
- id: 主键
- userId: 用户ID
- topic: 知识点
- difficulty: 难度等级
- attempts: 尝试次数
- successCount: 成功次数
- lastAttemptAt: 最后尝试时间
- createdAt: 创建时间

### 系统配置表 (system_configs)
- id: 主键
- configKey: 配置键
- configValue: 配置值
- description: 配置描述
- createdAt: 创建时间
- updatedAt: 更新时间

## 配置信息

### 应用配置
- 端口: 8080
- 文件上传限制: 10MB
- 数据库连接: localhost:3306/document_knowledge_db
- 默认用户名密码: root/root
- 缓存配置: Redis连接信息
- 消息队列: RabbitMQ/Kafka配置
- 日志级别: INFO/WARN/ERROR
- CORS配置: 跨域访问设置

### AI服务配置
- 模型: qwen-plus
- API密钥: 通过配置文件设置
- 请求超时: 30秒
- 最大重试次数: 3
- 模型参数调优配置

### 安全配置
- JWT密钥
- 密码加密盐值
- CSRF保护
- XSS防护
- 速率限制配置

### 监控配置
- Prometheus端点
- 告警阈值
- 日志收集配置
- 性能指标采集

## 部署说明

### 运行方式
```bash
# 开发环境运行
./mvnw spring-boot:run

# 生产环境打包部署
./mvnw clean package
java -jar target/spring-ai-alibaba-0.0.1-SNAPSHOT.jar

# Docker容器化部署
docker build -t spring-ai-alibaba .
docker run -p 8080:8080 spring-ai-alibaba
```

### 访问地址
http://localhost:8080

### 部署环境要求
- Java 21运行环境
- MySQL 8.0+数据库
- Redis缓存服务
- 阿里云DashScope API访问权限
- 至少4GB内存，推荐8GB以上

### 高可用部署
- 负载均衡配置
- 数据库主从复制
- Redis集群部署
- 微服务拆分建议
- 容器编排(Kubernetes)
- 自动扩缩容配置

## 特色功能

1. **多模态交互**: 支持纯文本和图片识别两种问答方式
2. **个性化教学**: 提供解释、示例、练习三种教学方法
3. **会话管理**: 支持多轮对话，保持上下文连贯性
4. **可视化辅助**: 自动生成教学图像增强理解
5. **知识库扩展**: 支持文档存储和检索
6. **智能推荐**: 基于学习数据分析的个性化推荐
7. **学习追踪**: 完整的学习行为记录和分析
8. **实时通信**: WebSocket支持的实时交互
9. **安全认证**: JWT Token的用户身份验证
10. **高可用架构**: 支持分布式部署和负载均衡

## 商业化增强功能

1. **订阅计费系统**: 支持按月/年订阅模式
2. **API服务**: 对外提供教育AI服务接口
3. **白标解决方案**: 支持OEM定制
4. **数据分析平台**: 教育机构学习数据洞察
5. **多租户支持**: 教育机构独立空间
6. **品牌定制**: UI/UX个性化定制
7. **第三方集成**: 支持与其他教育平台对接
8. **合规认证**: 符合教育数据隐私法规

## 适用场景

1. 小学数学辅导(加减乘除、分数、小数、几何图形等)
2. 小学语文知识点讲解
3. 应用题解析和解题过程展示
4. 作业题目分析和解答
5. 个性化练习题生成
6. 在线教育平台
7. 智能家教系统
8. 教育机构知识管理系统
9. 学生自主学习助手
10. 教师备课辅助工具

该系统为小学生提供了全方位的智能化学习辅助，通过多种交互方式和教学方法，帮助学生更好地理解和掌握知识点。同时具备商业化部署能力，可满足教育机构、在线教育平台等多种商业场景需求。