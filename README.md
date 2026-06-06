# AI情绪日记与干预平台

基于Spring Boot + Vue 3 + 通义千问AI的情绪管理与心理干预平台。

## 功能特性

- **情绪日记** - 记录每日情绪，AI智能分析
- **匿名树洞** - 匿名倾诉，情感交流
- **心理方案** - 专业调节方案推荐
- **AI对话** - 通义千问AI心理咨询
- **角色管理** - 学生/老师/管理员多角色

## 快速开始

### 环境要求

- JDK 11+
- Node.js 16+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd Emotional-Diary
```

### 2. 配置后端

```bash
# 复制配置模板
cp backend/src/main/resources/application-example.yml backend/src/main/resources/application.yml

# 编辑配置，填写数据库密码和API Key（可选）
```

**application.yml 关键配置：**
```yaml
spring:
  datasource:
    password: 你的数据库密码    # 修改这里

qwen:
  api-key: 你的通义千问APIKey   # 可选，不填则AI功能不可用
```

### 3. 初始化数据库

```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

默认账号：`admin / 123456`、`teacher01 / 123456`

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：http://localhost:8080/api

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：http://localhost:3000

## 项目结构

```
Emotional Diary/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/       # Java源码
│   ├── src/main/resources/  # 配置文件、SQL脚本
│   └── pom.xml              # Maven配置
├── frontend/                # Vue 3 前端
│   ├── src/                 # Vue源码
│   ├── package.json         # 依赖配置
│   └── vite.config.ts       # Vite配置
└── sql/                     # 数据库脚本
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7 + MyBatis + MySQL |
| 前端 | Vue 3 + TypeScript + Element Plus + Vite |
| AI | 阿里通义千问 (DashScope SDK) |

## 云部署（可选）

- **前端**: Vercel / Netlify（免费）
- **后端**: Render / Railway（免费）
- **数据库**: PlanetScale（免费）

详见 [部署指南](docs/deploy.md)

## License

MIT License
