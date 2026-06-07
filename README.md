# AI情绪日记与干预平台

基于 **Spring Boot + Vue 3 + 通义千问AI** 的情绪管理与心理干预平台，用于软件工程导论课程设计。

---

## 目录

- [项目概述](#项目概述)
- [功能模块](#功能模块)
- [技术架构](#技术架构)
- [数据库设计](#数据库设计)
- [API 接口](#api-接口)
- [环境搭建](#环境搭建)
- [运行步骤](#运行步骤)
- [默认账号](#默认账号)
- [项目结构](#项目结构)

---

## 项目概述

本平台是一个集情绪记录、AI智能分析、心理干预方案生成于一体的综合性心理健康管理系统。核心功能是通过 **阿里通义千问大模型 (Qwen-Max)** 对用户输入的情绪文本进行深度分析，自动识别情绪类型、评估风险等级，并实时生成个性化的情绪调节方案。

### 核心亮点

| 亮点 | 说明 |
|------|------|
| AI 情绪分析 | 调用通义千问 Qwen-Max 模型，分析情绪类型、强度、关键词、摘要 |
| 自动方案生成 | 分析完成后自动触发 AI 生成个性化调节方法、每日建议、推荐资源 |
| 风险评估 | 根据情绪分析结果自动计算风险等级（0-3级），给出相应建议 |
| 三级权限系统 | 学生 / 老师 / 管理员，不同角色拥有不同功能权限 |
| 匿名树洞 | 用户可匿名发布倾诉内容，互相评论鼓励 |
| 响应式设计 | 支持桌面端和移动端自适应布局 |

---

## 功能模块

### 1. 用户系统

#### 登录 / 注册
- 支持 Tab 切换的登录/注册一体化页面
- 注册时可选择身份角色：**学生** 或 **老师**
- 密码采用 SHA-256 加密存储
- Session 会话认证机制

#### 个人信息管理
- 头像展示（首字母头像）
- 查看/编辑昵称、真实姓名
- 修改密码功能
- 角色标签显示（学生/老师/管理员）

### 2. 情绪日记

- 记录每日心情和事件描述
- 日记列表按时间倒序排列
- 支持编辑和删除已有日记
- 日记详情查看

### 3. AI 智能分析（核心功能）

#### 页面布局
- **左侧输入区**：文本输入框（最多3000字）+ 开始分析按钮
- **右侧结果区**：三段式卡片展示

#### 分析流程
```
用户输入文本 → AI 情绪分析 → 显示结果 → 自动生成调节方案 → 展示完整报告
```

#### 输出内容

**(1) 情绪识别**
- 情绪类型标签：开心 / 平静 / 焦虑 / 悲伤 / 愤怒 / 恐惧
- 情绪强度评分：0-10 分（圆环进度条可视化）
- 情绪摘要：AI 生成的文字总结
- 关键词提取：最多4个关键词标签

**(2) 风险评估**
- 风险等级：0（正常）/ 1（低风险）/ 2（中风险）/ 3（高风险）
- 渐变进度条可视化
- 风险建议文案

**(3) 个性化调节方案（自动生成）**
- 方案标题
- 调节方法列表（编号卡片）：
  - 方法名称
  - 时长（分钟）
  - 难度等级（初级/中级/高级）
  - 方法描述
- 每日建议（列表形式）
- 推荐资源（标签形式）

### 4. 匿名树洞

- 匿名发布倾诉内容
- 其他用户可以浏览、点赞、评论
- 评论支持互相鼓励的互动模式
- 老师可进行审核管理

### 5. 心理方案库

- 浏览预设的情绪调节方案
- 方案分类展示
- 方案详情查看
- 管理员可增删改查方案

### 6. 管理后台（老师/管理员）

| 功能 | 老师 | 管理员 |
|------|------|--------|
| 用户管理 | ✅ | ✅ |
| 审核管理（树洞审核） | ✅ | ✅ |
| 方案管理 | ❌ | ✅ |
| 系统配置 | ❌ | ✅ |

---

## 技术架构

### 整体架构

```
┌─────────────────────────────────────────────┐
│                  浏览器                       │
│         Vue 3 + Element Plus + Vite          │
└──────────────────┬──────────────────────────┘
                   │ HTTP (RESTful API)
                   ▼
┌─────────────────────────────────────────────┐
│             Spring Boot 后端                  │
│  ┌─────────┐ ┌──────────┐ ┌──────────────┐ │
│  │Controller│→│ Service  │→│   Mapper     │ │
│  └─────────┘ └──────────┘ └──────────────┘ │
│                    │                        │
│           ┌────────▼────────┐               │
│           │  QwenAiService  │ ← DashScope   │
│           └─────────────────┘               │
└──────────────────┬──────────────────────────┘
                   │ JDBC
                   ▼
┌─────────────────────────────────────────────┐
│              MySQL 8.0 数据库                 │
└─────────────────────────────────────────────┘
```

### 技术栈详情

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **后端框架** | Spring Boot | 2.7.x | 应用主框架 |
| **ORM** | MyBatis | - | 数据持久层 |
| **数据库** | MySQL | 8.0+ | 数据存储 |
| **前端框架** | Vue 3 | 3.x | SPA 单页应用 |
| **语言** | TypeScript | 5.x | 前端类型安全 |
| **UI 库** | Element Plus | 2.x | 组件库 |
| **构建工具** | Vite | 5.x | 前端构建 |
| **样式** | SCSS | - | 样式预处理 |
| **AI 模型** | 通义千问 Qwen-Max | - | 情绪分析与方案生成 |
| **AI SDK** | DashScope | - | 阿里云 AI SDK |

### 关键依赖

**后端 (pom.xml):**
- `spring-boot-starter-web` - Web 服务
- `mybatis-spring-boot-starter` - MyBatis 集成
- `mysql-connector-java` - MySQL 驱动
- `dashscope-sdk-java` - 通义千问 SDK

**前端 (package.json):**
- `vue` + `vue-router` - 核心框架与路由
- `element-plus` - UI 组件库
- `axios` - HTTP 请求
- `sass` - SCSS 编译
- `@vitejs/plugin-vue` - Vite Vue 插件

---

## 数据库设计

### 数据表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| emotion_diary | 情绪日记表 |
| treehole_post | 树洞帖子表 |
| treehole_comment | 树洞评论表 |
| solution | 方案表 |

### sys_user 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键自增 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(200) | 密码（SHA-256加密） |
| nickname | VARCHAR(50) | 昵称 |
| real_name | VARCHAR(50) | 真实姓名 |
| role | INT | 角色（0=学生, 1=老师, 2=管理员） |
| avatar | VARCHAR(255) | 头像URL |
| status | INT | 状态（0=禁用, 1=正常） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### emotion_diary 日记表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键自增 |
| user_id | BIGINT | 用户ID（外键） |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容 |
| emotion_type | VARCHAR(20) | AI分析的情绪类型 |
| emotion_score | INT | 情绪强度(0-10) |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## API 接口

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 用户登录 |
| POST | `/auth/register` | 用户注册 |
| GET | `/auth/info` | 获取当前用户信息 |
| PUT | `/auth/profile` | 更新个人信息 |

### AI 分析接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/ai/analyze/content` | AI 情绪内容分析 |
| POST | `/ai/generate/solution` | AI 生成调节方案 |

### 日记接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/diary/list` | 获取日记列表 |
| POST | `/diary/create` | 创建日记 |
| PUT | `/diary/update/{id}` | 更新日记 |
| DELETE | `/diary/delete/{id}` | 删除日记 |
| GET | `/diary/{id}` | 获取日记详情 |

### 树洞接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/treehole/posts` | 获取帖子列表 |
| POST | `/treehole/post` | 发布帖子 |
| POST | `/treehole/comment` | 发表评论 |
| POST | `/treehole/like/{id}` | 点赞 |

### 方案接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/solution/list` | 获取方案列表 |
| GET | `/solution/{id}` | 获取方案详情 |
| POST | `/solution/create` | 创建方案（管理员） |
| PUT | `/solution/update/{id}` | 更新方案（管理员） |
| DELETE | `/solution/delete/{id}` | 删除方案（管理员） |

### 用户管理接口（老师/管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/users` | 用户列表 |
| PUT | `/admin/user/{id}/role` | 修改用户角色 |
| PUT | `/admin/user/{id}/status` | 启用/禁用用户 |

---

## 环境搭建

### 必要软件

| 软件 | 版本要求 | 下载地址 |
|------|----------|----------|
| JDK | 11+ | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [Adoptium](https://adoptium.net/) |
| Node.js | 16+ | [Node.js 官网](https://nodejs.org/) |
| MySQL | 8.0+ | [MySQL 官网](https://dev.mysql.com/downloads/) |
| Maven | 3.6+ | 通常随 IDE 内置，或 [Maven 官网](https://maven.apache.org/download.cgi) |
| Git | 最新版 | [Git 官网](https://git-scm.com/) |

### 开发工具（可选但推荐）

| 工具 | 说明 |
|------|------|
| IntelliJ IDEA | Java 后端开发（推荐） |
| VS Code | 前端开发（推荐） |
| Navicat / DBeaver | 数据库可视化管理 |
| Postman / Apifox | API 接口测试 |

---

## 运行步骤

### 第一步：克隆项目

```bash
git clone https://github.com/MaElen0309/AI_emotional_diary_-_intervention_platform.git
cd AI_emotional_diary_-_intervention_platform
```

### 第二步：数据库初始化

1. 确保 MySQL 服务已启动
2. 创建数据库（如不存在）：

```sql
CREATE DATABASE IF NOT EXISTS emotional_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 导入初始化数据：

```bash
mysql -u root -p emotional_diary < backend/src/main/resources/sql/init.sql
```

或手动在 MySQL 客户端中执行 `backend/src/main/resources/sql/init.sql` 文件。

### 第三步：配置后端

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/emotional_diary?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root        # 你的MySQL用户名
    password: 1234     # 你的MySQL密码


dashscope:
  api-key: sk-xxxxxxxxxxxxxxxxxxxxxxxx
```

> **获取通义千问 API Key：**
> 1. 访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)
> 2. 登录并创建 API Key
> 3. 将 Key 填入上面的配置

### 第四步：启动后端

```bash
cd backend
mvn spring-boot:run
```

或在 IDEA 中直接运行 `EmotionalDiaryApplication.java` 的 main 方法。

后端启动成功后访问：`http://localhost:8080`

### 第五步：安装前端依赖

```bash
cd frontend
npm install
```

### 第六步：启动前端

```bash
npm run dev
```

前端启动成功后访问：`http://localhost:3000`

### 第七步：使用系统

1. 打开浏览器访问 `http://localhost:3000`
2. 使用默认账号登录（见下方账号表）
3. 进入「AI分析」页面，输入情绪文本，点击「开始分析」
4. 等待 AI 分析完成，自动生成调节方案

---

## 默认账号

| 角色 | 用户名 | 密码 | 可见菜单 |
|------|--------|------|----------|
| 管理员 | admin | 123456 | 全部菜单（含系统配置） |
| 老师 | teacher01 | 123456 | 全部菜单（除系统配置） |
| 学生 | student01 | 123456 | 仪表盘、日记、树洞、方案库、AI分析 |

---

## 项目结构

```
Emotional Diary/
│
├── backend/                                  # Spring Boot 后端
│   ├── pom.xml                               # Maven 依赖配置
│   └── src/
│       ├── main/
│       │   ├── java/com/emotional/diary/
│       │   │   ├── EmotionalDiaryApplication.java   # 启动类
│       │   │   │
│       │   │   ├── controller/                # 控制器层（接口定义）
│       │   │   │   ├── AuthController.java    #   认证接口（登录/注册/个人信息）
│       │   │   │   ├── AiController.java      #   AI 分析接口
│       │   │   │   ├── DiaryController.java   #   日记 CRUD 接口
│       │   │   │   ├── TreeHoleController.java #  树洞接口
│       │   │   │   ├── SolutionController.java #  方案管理接口
│       │   │   │   └── AdminController.java   #   管理后台接口
│       │   │   │
│       │   │   ├── service/                   # 业务逻辑层
│       │   │   │   ├── UserService.java       #   用户服务
│       │   │   │   ├── DiaryService.java      #   日记服务
│       │   │   │   ├── TreeHoleService.java   #   树洞服务
│       │   │   │   ├── SolutionService.java   #   方案服务
│       │   │   │   └── QwenAiService.java     #   ★ 通义千问 AI 服务（核心）
│       │   │   │
│       │   │   ├── mapper/                    # 数据访问层（MyBatis）
│       │   │   │   ├── UserMapper.java
│       │   │   │   ├── DiaryMapper.java
│       │   │   │   ├── TreeHolePostMapper.java
│       │   │   │   ├── TreeHoleCommentMapper.java
│       │   │   │   └── SolutionMapper.java
│       │   │   │
│       │   │   ├── entity/                    # 实体类
│       │   │   │   ├── User.java              #   用户实体
│       │   │   │   ├── Diary.java             #   日记实体
│       │   │   │   ├── TreeHolePost.java      #   树洞帖子实体
│       │   │   │   ├── TreeHoleComment.java   #   树洞评论实体
│       │   │   │   └── Solution.java          #   方案实体
│       │   │   │
│       │   │   └── config/                    # 配置类
│       │   │       ├── SecurityConfig.java    #   安全配置（CORS/Session/权限）
│       │   │       ├── WebMvcConfig.java      #   Web MVC 配置
│       │   │       └── DatabaseInitializer.java # 数据库自动初始化
│       │   │
│       │   └── resources/
│       │       ├── application.yml            # 应用配置文件
│       │       ├── sql/
│       │       │   ├── init.sql               #   MySQL 初始化脚本
│       │       │   └── init-postgres.sql      #   PostgreSQL 初始化脚本
│       │       └── mapper/
│       │           ├── UserMapper.xml
│       │           ├── DiaryMapper.xml
│       │           ├── TreeHolePostMapper.xml
│       │           ├── TreeHoleCommentMapper.xml
│       │           └── SolutionMapper.xml
│       │
│       └── test/                              # 测试代码
│
├── frontend/                                 # Vue 3 前端
│   ├── package.json                          # NPM 依赖配置
│   ├── vite.config.ts                        # Vite 构建配置
│   ├── tsconfig.json                         # TypeScript 配置
│   ├── index.html                            # 入口 HTML
│   └── src/
│       ├── main.ts                           # 应用入口
│       ├── App.vue                           # 根组件
│       ├── router/
│       │   └── index.ts                      #   路由配置（含权限守卫）
│       ├── utils/
│       │   └── request.ts                    #   Axios 封装（拦截器/ baseURL）
│       ├── styles/
│       │   └── global.scss                   #   全局样式变量
│       └── views/
│           ├── login/
│           │   └── LoginView.vue             #   登录/注册页面（Tab切换）
│           ├── layout/
│           │   └── MainLayout.vue            #   主布局（侧边栏+顶栏+内容区）
│           ├── dashboard/
│           │   └── DashboardView.vue         #   仪表盘首页
│           ├── ai/
│           │   └── AiAnalysis.vue            #   ★ AI 情绪分析页（核心功能）
│           ├── diary/
│           │   ├── DiaryListView.vue         #   日记列表
│           │   └── DiaryEditorView.vue       #   写/编辑日记
│           ├── treehole/
│           │   └── TreeHoleView.vue          #   匿名树洞
│           ├── solution/
│           │   └── SolutionListView.vue      #   方案库
│           ├── profile/
│           │   └── ProfileView.vue           #   个人信息管理
│           └── admin/
│               ├── UserManageView.vue        #   用户管理
│               ├── ReviewManageView.vue      #   审核管理
│               └── SystemConfigView.vue      #   系统配置
│
├── Dockerfile                                # Docker 构建文件（前后端一体打包）
├── .dockerignore                             # Docker 构建忽略文件
├── .env.production                           # 生产环境变量示例
└── README.md                                 # 本文件
```
