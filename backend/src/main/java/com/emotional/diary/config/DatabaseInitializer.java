package com.emotional.diary.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.stream.Collectors;

/**
 * 数据库自动初始化器
 * 检测到 PostgreSQL 时，自动执行 init-postgres.sql 建表和插入初始数据
 * 仅在 Render 等云平台首次部署时运行
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String dbProductName = metaData.getDatabaseProductName();

            // 仅对 PostgreSQL 执行初始化
            if (dbProductName != null && dbProductName.contains("PostgreSQL")) {
                logger.info("检测到 PostgreSQL 数据库，开始初始化...");

                // 检查是否已经初始化过（检查 sys_user 表是否存在）
                boolean alreadyInit = false;
                try {
                    Integer count = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'sys_user'",
                            Integer.class);
                    alreadyInit = count != null && count > 0;
                } catch (Exception e) {
                    // 表不存在，需要初始化
                }

                if (!alreadyInit) {
                    executeSqlScript("sql/init-postgres.sql");
                    logger.info("PostgreSQL 数据库初始化完成！");
                } else {
                    logger.info("PostgreSQL 数据库已存在，跳过初始化");
                }
            } else {
                logger.info("使用 {} 数据库，跳过自动初始化", dbProductName);
            }
        } catch (Exception e) {
            logger.warn("数据库初始化检查失败: {}", e.getMessage());
        }
    }

    private void executeSqlScript(String classPath) throws Exception {
        ClassPathResource resource = new ClassPathResource(classPath);
        if (!resource.exists()) {
            logger.warn("SQL 脚本不存在: {}", classPath);
            return;
        }

        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        }

        // 分割 SQL 语句并逐条执行
        String[] statements = sql.split(";");
        for (String statement : statements) {
            String trimmed = statement.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    jdbcTemplate.execute(trimmed);
                } catch (Exception e) {
                    logger.debug("SQL 执行（可能已存在）: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                }
            }
        }
    }
}
