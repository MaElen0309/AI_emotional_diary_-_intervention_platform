# AI情绪日记与干预平台

<p align="center">
  <strong>基于 Spring Boot + Vue 3 + 通义千问AI 的情绪管理与心理干预平台</strong>
  <br>
  <em>现代化UI设计 · 全端响应式 · 渐变主题 · 流畅动画</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7-green" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Vue.js-3.x-blue" alt="Vue 3" />
  <img src="https://img.shields.io/badge/MySQL-8.0+-orange" alt="MySQL" />
  <img src="https://img.shields.io/badge/AI-通义千问-purple" alt="通义千问" />
  <img src="https://img.shields.io/badge/UI-Modern%20Design-pink" alt="Modern UI" />
</p>

---

## 项目简介

本平台是一个面向高校学生的**情绪管理与心理干预系统**，结合人工智能技术，为学生提供情绪记录、智能分析、匿名倾诉和个性化心理方案推荐服务。

### 核心功能

| 模块 | 功能描述 |
|------|----------|
| 情绪日记 | 记录每日情绪状态，支持文字/图片，AI智能分析情绪趋势 |
| 匿名树洞 | 匿名发布情感内容，保护隐私，互相鼓励 |
| 心理方案 | 基于情绪数据推荐专业调节方案（音乐、运动、冥想等） |
| AI对话 | 集成通义千问大模型，提供智能心理咨询与情感陪伴 |
| 用户管理 | 学生/老师/管理员三角色权限体系 |

### UI设计亮点

- **深色渐变侧边栏** - 紫色系主题 (`#6c63ff`)，从暗蓝到靛青的180度渐变
- **分屏登录页** - 桌面端左右布局（品牌展示+表单），移动端品牌区置顶
- **仪表盘欢迎横幅** - 渐变背景 + 悬浮统计卡片 + hover上浮动画
- **全端响应式** - 移动端抽屉菜单 + 自适应网格 + 触摸优化
- **统一设计语言** - 12-16px圆角 / 多层柔和阴影 / 滚动条美化 / Element Plus主题覆盖

### 系统截图

<!-- 可在此处添加项目截图 -->
<!--
<p align="center">
  <img src="./screenshots/login.png" width="400" alt="登录页" />
  <img src="./screenshots/dashboard.png" width="400" alt="仪表盘" />
</p>
-->

---

## 技术架构

```
┌─────────────────────────────────────────────────────┐
│                    前端 (Frontend)                    │
│  Vue 3 + TypeScript + Element Plus + Vite            │
│  路由守卫 / Axios拦截器 / 响应式布局                   │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP / RESTful API
┌──────────────────────▼──────────────────────────────┐
│                    后端 (Backend)                     │
│  Spring Boot 2.7 + MyBatis + Session认证             │
│  Controller → Service → Mapper 三层架构               │
└──────────────────────┬──────────────────────────────┘
                       │ JDBC
┌──────────────────────▼──────────────────────────────┐
│                  数据库 (Database)                    │
│  MySQL 8.0+                                          │
│  sys_user / emotion_diary / treehole_content / ...   │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP API
┌──────────────────────▼──────────────────────────────┐
│              AI 服务 (Alibaba Cloud)                 │
│  通义千问 (Qwen-Max) via DashScope SDK                │
└─────────────────────────────────────────────────────┘
```

### 技术栈详情

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 2.7.x | 核心框架 |
| **ORM** | MyBatis | - | 数据持久层 |
| **数据库** | MySQL | 8.0+ | 数据存储 |
| **前端框架** | Vue.js | 3.x | 渐进式框架 |
| **UI组件库** | Element Plus | - | 企业级UI |
| **构建工具** | Vite | 5.x | 前端构建 |
| **语言** | TypeScript | - | 类型安全 |
| **AI引擎** | 通义千问 | Qwen-Max | 大语言模型 |

---

## 快速开始

### 环境要求

- [JDK 11+](https://adoptium.net/)（推荐 JDK 17）
- [Node.js 16+](https://nodejs.org/)（推荐 LTS 版本）
- [MySQL 8.0+](https://dev.mysql.com/downloads/)
- Maven 3.6+

### 第一步：克隆项目

```bash
git clone https://github.com/MaElen0309/AI_emotional_diary_-_intervention_platform.git
cd AI_emotional_diary_-_intervention_platform
```

### 第二步：配置后端

```bash
# 复制配置模板
cp backend/src/main/resources/application-example.yml backend/src/main/resources/application.yml

# 编辑配置文件
nano backend/src/main/resources/application.yml
```

**需要修改的配置项：**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/emotional_diary?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码          # ← 修改这里

qwen:
  api-key: 你的通义千问API Key     # ← 可选，不填则AI功能不可用
  model: qwen-max
```

> **获取通义千问API Key：**
> 访问 [阿里云 DashScope 控制台](https://dashscope.console.aliyun.com/) 免费申请

### 第三步：初始化数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库并导入初始化脚本
source backend/src/main/resources/sql/init.sql;
```

或者一行命令：

```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

**默认账号：**

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 老师 | teacher01 | 123456 |

### 第四步：启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动成功后访问：http://localhost:8080/api

### 第五步：启动前端

```bash
cd frontend
npm install        # 安装依赖（首次运行）
npm run dev        # 启动开发服务器
```

前端启动成功后访问：http://localhost:3001

---

## 项目结构

```
AI_emotional_diary_-_intervention_platform/
│
├── backend/                          # 后端项目
│   ├── pom.xml                       # Maven配置
│   ├── Procfile                      # 云平台启动配置
│   ├── render.yaml                   # Render部署配置
│   ├── railway.toml                  # Railway部署配置
│   └── src/main/
│       ├── java/com/emotional/diary/
│       │   ├── DiaryApplication.java         # 启动类
│       │   ├── config/                       # 配置类
│       │   │   ├── SecurityConfig.java       # 安全配置
│       │   │   └── WebConfig.java            # Web/CORS配置
│       │   ├── controller/                   # 控制器层
│       │   │   ├── AuthController.java       # 认证接口
│       │   │   ├── EmotionDiaryController.java # 日记接口
│       │   │   ├── AiController.java         # AI对话接口
│       │   │   ├── TreeholeController.java    # 树洞接口
│       │   │   ├── SolutionController.java    # 方案接口
│       │   │   └── UserController.java        # 用户接口
│       │   ├── entity/                       # 实体类
│       │   ├── mapper/                       # MyBatis Mapper
│       │   ├── service/                      # 业务逻辑层
│       │   │   ├── UserService.java          # 用户服务
│       │   │   ├── QwenAiService.java        # AI服务(通义千问)
│       │   │   └── ...
│       │   ├── interceptor/                  # 拦截器
│       │   │   └── AuthInterceptor.java      # 认证拦截器
│       │   └── common/                       # 公共类
│       │       ├── Result.java               # 统一响应
│       │       └── PageInfo.java             # 分页封装
│       └── resources/
│           ├── application.yml               # 应用配置
│           ├── application-example.yml       # 配置模板
│           ├── sql/init.sql                  # 数据库初始化脚本
│           └── mapper/*.xml                  # MyBatis XML映射
│
├── frontend/                         # 前端项目
│   ├── package.json                  # NPM依赖配置
│   ├── vite.config.ts                # Vite构建配置
│   ├── vercel.json                   # Vercel部署配置
│   ├── netlify.toml                  # Netlify部署配置
│   ├── .env.example                  # 环境变量模板
│   └── src/
│       ├── main.ts                   # 入口文件
│       ├── App.vue                   # 根组件
│       ├── router/index.ts           # 路由配置
│       ├── utils/request.ts          # Axios请求封装
│       ├── styles/global.scss        # 全局样式
│       └── views/                    # 页面组件
│           ├── login/LoginView.vue           # 登录页
│           ├── layout/MainLayout.vue         # 主布局
│           ├── dashboard/DashboardView.vue   # 仪表盘
│           ├── diary/DiaryCreate.vue         # 写日记
│           ├── diary/DiaryList.vue           # 日记列表
│           ├── ai/AiAnalysis.vue             # AI分析
│           ├── treehole/TreeholeList.vue     # 树洞
│           ├── solution/SolutionList.vue     # 方案列表
│           └── admin/                        # 管理后台
│               ├── UserManage.vue
│               ├── AuditManage.vue
│               ├── SolutionManage.vue
│               └── SystemConfig.vue
│
├── .gitignore                       # Git忽略规则
└── README.md                        # 本文档
```

---

## API接口文档

### 认证模块 `/api/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 用户登录 | 否 |
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/logout` | 退出登录 | 是 |

### 日记模块 `/api/diaries`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/diaries` | 获取日记列表 | 是 |
| POST | `/api/diaries` | 创建日记 | 是 |
| GET | `/api/diaries/{id}` | 获取日记详情 | 是 |
| DELETE | `/api/diaries/{id}` | 删除日记 | 是 |

### AI模块 `/api/ai`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/ai/chat` | AI对话 | 是 |
| POST | `/api/ai/analyze` | 情绪分析 | 是 |

### 树洞模块 `/api/treehole`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/treehole` | 获取树洞列表 | 是 |
| POST | `/api/treehole` | 发布树洞内容 | 是 |

---

## 云部署指南

### 一键部署到免费云平台

#### 方案A：Vercel + Render（推荐）

| 组件 | 平台 | 费用 |
|------|------|------|
| 前端 | Vercel | 免费 |
| 后端 | Render | 免费 |
| 数据库 | PlanetScale | 免费 |

**部署步骤：**

<details>
<summary><b>点击展开详细步骤</b></summary>

1. **准备数据库（PlanetScale）**
   - 注册 [PlanetScale](https://planetscale.com/)
   - 创建数据库 `emotional_diary`
   - 在控制台执行 `init.sql` 初始化脚本
   - 复制连接信息

2. **部署后端（Render）**
   - 注册 [Render](https://render.com/)
   - New → Web Service → 连接GitHub仓库
   - 配置：
     ```
     Root Directory: backend
     Build Command: mvn clean package -DskipTests
     Start Command: java -jar target/diary-platform-1.0.0.jar
     ```
   - 添加环境变量：
     ```
     JDBC_DATABASE_URL = PlanetScale连接地址
     DATABASE_USERNAME = 数据库用户名
     DATABASE_PASSWORD = 数据库密码
     QWEN_API_KEY = 通义千问API Key（可选）
     ```

3. **部署前端（Vercel）**
   - 注册 [Vercel](https://vercel.com/)
   - New Project → Import Git Repository
   - 配置：
     ```
     Root Directory: frontend
     Framework Preset: Vite
     ```
   - 添加环境变量：
     ```
     VITE_API_BASE_URL = https://你的后端地址.onrender.com/api
     ```

4. 访问前端域名即可使用！

</details>

#### 方案B：Netlify + Railway

| 组件 | 平台 | 费用 |
|------|------|------|
| 前端 | Netlify | 免费 |
| 后端 | Railway | 免费 |

项目已包含对应的配置文件（`netlify.toml`、`railway.toml`），直接导入即可。

---

## 开发指南

### 本地开发调试

```bash
# 终端1：启动后端（热重载）
cd backend
mvn spring-boot:run

# 终端2：启动前端（热更新）
cd frontend
npm run dev
```

### 构建生产版本

```bash
# 构建前端
cd frontend
npm run build

# 构建后端JAR包
cd backend
mvn clean package -DskipTests

# 输出文件：backend/target/diary-platform-1.0.0.jar
```

### 常见问题

<details>
<summary><b>登录报401错误？</b></summary>

检查以下几项：

1. 数据库是否已初始化？执行 `init.sql`
2. SecurityConfig 是否正确放行 `/auth/**` 路径？
3. 前端代理配置是否正确？（开发环境使用Vite proxy）

</details>

<details>
<summary><b>AI功能不可用？</b></summary>

1. 检查 `application.yml` 中 `qwen.api-key` 是否已填写
2. API Key 是否有效？访问 [DashScope控制台](https://dashscope.console.aliyun.com/) 查看
3. 检查网络是否能访问阿里云API

</details>

<details>
<summary><b>CORS跨域错误？</b></summary>

确保后端 `WebConfig.java` 中已配置CORS允许前端来源：

```java
allowedOrigins.add("http://localhost:3001");  // 开发环境
allowedOrigins.add("https://你的域名.vercel.app"); // 生产环境
```

</details>

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 许可证

本项目采用 MIT 许可证开源。详见 [LICENSE](LICENSE) 文件。

---

## 致谢

- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Spring Boot](https://spring.io/projects/spring-boot) - Java应用框架
- [Element Plus](https://element-plus.org/) - Vue 3 UI组件库
- [阿里云通义千问](https://dashscope.console.aliyun.com/) - 大语言模型API
- [DashScope SDK](https://github.com/aliyun/alibabacloud-dashscope-sdk-java) - Java SDK

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/MaElen0309">MaElen0309</a>
</p>
