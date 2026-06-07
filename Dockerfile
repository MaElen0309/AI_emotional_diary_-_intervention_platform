# ============================================
# AI情绪日记与干预平台 - Docker 部署配置
# 多阶段构建：前端构建 + 后端打包
# ============================================

# ========== 阶段1: 构建Vue前端 ==========
FROM node:18-alpine AS frontend-builder

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com

COPY frontend/ ./
RUN npm run build

# ========== 阶段2: 构建Spring Boot后端 ==========
FROM maven:3.8-openjdk-11 AS backend-builder

WORKDIR /app

COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

COPY backend/src ./src
RUN mvn package -DskipTests -B

# ========== 阶段3: 运行镜像 ==========
FROM openjdk:11-jre-slim

WORKDIR /app

# 复制后端JAR包
COPY --from=backend-builder /app/target/*.jar app.jar

# 复制前端构建产物到静态资源目录
RUN mkdir -p /app/static
COPY --from=frontend-builder /app/frontend/dist/ /app/static/

# 创建上传目录
RUN mkdir -p /app/uploads

EXPOSE 8080

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
