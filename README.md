# AI情绪日记与干预平台

基于 **Spring Boot + Vue 3 + 通义千问AI** 的情绪管理与心理干预平台。

## 功能

- 情绪日记（AI智能分析情绪、自动生成调节方案）
- 匿名树洞（匿名倾诉、互相鼓励）
- 心理方案库（个性化调节方案推荐）
- AI对话分析（集成通义千问大模型）
- 用户管理（学生/老师/管理员三级权限）
- 用户注册 / 个人信息编辑

## 技术栈

| 后端 | 前端 | 数据库 | AI |
|------|------|--------|-----|
| Spring Boot 2.7 | Vue 3 + TypeScript | MySQL 8.0 / PostgreSQL | 通义千问 Qwen-Max |
| MyBatis | Element Plus + Vite | | DashScope SDK |

## 快速开始

### 环境要求
JDK 11+ / Node.js 16+ / MySQL 8.0+

### 1. 克隆 & 安装
```bash
git clone https://github.com/MaElen0309/AI_emotional_diary_-_intervention_platform.git
cd AI_emotional_diary_-_intervention_platform/frontend && npm install
```

### 2. 数据库初始化
```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

### 3. 启动后端
```bash
cd backend && mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend && npm run dev
```

访问 http://localhost:3000

### 默认账号
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 老师 | teacher01 | 123456 |

## 云部署 (Docker)

项目包含 `Dockerfile` 和 `render.yaml`，支持一键部署到 Render / Sealos 等平台：

```bash
docker build -t emotional-diary .
docker run -p 8080:8080 emotional-diary
```

**Sealos 部署（免费，无需信用卡）：**
1. 注册 https://cloud.sealos.io
2. 新建应用 → 选择 GitHub 仓库 → Dockerfile 构建
3. 配置数据库环境变量即可访问

## 项目结构
```
├── backend/          # Spring Boot 后端
│   └── src/main/
│       ├── java/     # Controller / Service / Mapper / Config
│       └── resources/# application.yml / sql/ / mapper/
├── frontend/         # Vue 3 前端
│   └── src/          # views / router / utils / styles
├── Dockerfile        # Docker 构建配置
└── render.yaml       # Render 部署配置
```
