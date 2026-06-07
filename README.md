# AI情绪日记与干预平台

基于 **Spring Boot + Vue 3 + 通义千问AI** 的情绪管理与心理干预平台。

## 功能特性

- **情绪日记** - 记录每日心情，AI智能分析情绪类型与强度
- **自动调节方案** - 分析后自动生成个性化调节方法、每日建议和推荐资源
- **匿名树洞** - 匿名倾诉，互相鼓励
- **心理方案库** - 浏览和管理情绪调节方案
- **AI对话分析** - 集成通义千问 Qwen-Max 大模型
- **三级权限** - 学生 / 老师 / 管理员
- **注册登录** - 支持新用户注册和个人信息管理

## 技术栈

| 层 | 技术 |
|---|------|
| 后端 | Spring Boot 2.7 + MyBatis |
| 前端 | Vue 3 + TypeScript + Element Plus + Vite |
| 数据库 | MySQL 8.0 / PostgreSQL |
| AI | 通义千问 Qwen-Max (DashScope SDK) |

## 快速开始

### 环境要求

- JDK 11+
- Node.js 16+
- MySQL 8.0+ (或 PostgreSQL)

### 1. 克隆项目

```bash
git clone https://github.com/MaElen0309/AI_emotional_diary_-_intervention_platform.git
cd AI_emotional_diary_-_intervention_platform
```

### 2. 安装前端依赖

```bash
cd frontend && npm install
```

### 3. 初始化数据库

**MySQL:**
```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

**PostgreSQL:**
```bash
psql -U postgres < backend/src/main/resources/sql/init-postgres.sql
```

### 4. 启动后端

```bash
cd backend && mvn spring-boot:run
```

后端运行在 `http://localhost:8080`

### 5. 启动前端

```bash
cd frontend && npm run dev
```

前端运行在 `http://localhost:3000`

### 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 老师 | teacher01 | 123456 |

## 云部署

### Docker 本地构建

```bash
docker build -t emotional-diary .
docker run -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_PORT=3306 \
  -e DB_NAME=emotional_diary \
  -e DB_USER=root \
  -e DB_PASSWORD=your-password \
  emotional-diary
```

### Sealos 部署（免费，无需信用卡）

1. 注册 [Sealos](https://cloud.sealos.io)
2. 新建应用 → 选择 GitHub 仓库 → 自动检测 Dockerfile 构建
3. 创建免费 MySQL 数据库并配置连接信息
4. 部署完成后获取公网访问地址

### Render 部署

1. 注册 [Render](https://dashboard.render.com)
2. 连接 GitHub 仓库
3. Language 选 `Docker`，Instance Type 选 `Free`
4. 自动构建部署

## 项目结构

```
Emotional Diary/
├── backend/                          # Spring Boot 后端
│   └── src/main/
│       ├── java/com/emotional/diary/
│       │   ├── controller/           # 接口层
│       │   ├── service/              # 业务逻辑层（含AI服务）
│       │   ├── mapper/               # 数据访问层
│       │   ├── entity/               # 实体类
│       │   └── config/               # 配置类（安全/CORS/AI）
│       └── resources/
│           ├── application.yml       # 应用配置
│           ├── sql/                  # 数据库脚本
│           └── mapper/               # MyBatis XML
├── frontend/                         # Vue 3 前端
│   └── src/
│       ├── views/                    # 页面组件
│       │   ├── login/                # 登录/注册
│       │   ├── dashboard/            # 仪表盘
│       │   ├── ai/                   # AI分析（含自动方案生成）
│       │   ├── diary/                # 情绪日记
│       │   ├── treehole/             # 匿名树洞
│       │   ├── solution/             # 方案库
│       │   ├── profile/              # 个人信息
│       │   ├── admin/                # 管理后台
│       │   └── layout/               # 主布局
│       ├── router/                   # 路由配置
│       ├── utils/                    # 工具函数
│       └── styles/                   # 全局样式
├── Dockerfile                        # Docker 构建文件
├── render.yaml                       # Render 部署配置
└── README.md                         # 项目说明
```
