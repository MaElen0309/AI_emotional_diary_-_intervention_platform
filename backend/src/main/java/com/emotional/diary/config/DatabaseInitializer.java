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
                initSolutionData(); // 初始化方案库数据
                initFavoriteTable(); // 确保收藏表存在
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

    /**
     * 初始化方案库数据（仅在方案库为空时插入）
     */
    private void initSolutionData() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM solution", Integer.class);
            if (count != null && count > 0) {
                logger.info("方案库已有 {} 条数据，跳过初始化", count);
                return;
            }

            logger.info("方案库为空，开始初始化预设方案...");

            String[][] solutions = {
                    { "4-7-8 呼吸放松法", "通过特定的呼吸节奏快速缓解焦虑和压力",
                            "## 4-7-8 呼吸法\n\n这是一种简单而有效的放松技巧。\n\n### 操作步骤：\n1. 准备姿势：坐直或躺下，舌尖轻触上颚门牙后方\n2. 吸气：用鼻子完全吸气，心中默数 4 秒\n3. 屏息：屏住呼吸，默数 7 秒\n4. 呼气：用嘴缓慢呼出（发出呼声），默数 8 秒\n5. 重复：以上为一个循环，重复 4 次",
                            "呼吸调节", "焦虑,恐惧" },
                    { "腹式深呼吸训练", "学习正确的腹式呼吸方式，激活副交感神经",
                            "## 腹式深呼吸法\n\n腹式呼吸能激活身体的放松反应。\n\n### 训练方法：\n1. 手部定位：一只手放胸口，另一只手放肚脐处\n2. 吸气：用鼻子缓慢吸气，感受腹部隆起\n3. 呼气：用嘴缓慢呼出，感受腹部回落\n4. 节奏：吸气4秒 → 屏息2秒 → 呼气6秒",
                            "呼吸调节", "焦虑,愤怒,悲伤" },
                    { "正念冥想入门", "通过专注当下觉察，减少情绪内耗",
                            "## 正念冥想引导\n\n正念是一种有意识地、不加评判地关注当下的练习方式。\n\n### 10分钟入门练习：\n- 第1-2分钟：身体扫描\n- 第3-5分钟：呼吸观察\n- 第6-8分钟：声音觉察\n- 第9-10分钟：整体安住",
                            "冥想放松", "焦虑,悲伤,平静" },
                    { "ABC认知重构法", "识别并改变非理性思维模式",
                            "## ABC 认知重构法\n\n这是认知行为疗法(CBT)的核心技术。\n\n### ABC模型说明：\n- A 触发事件：发生了什么事\n- B 信念/想法：你对这件事的看法\n- C 结果：你的情绪和行为反应\n\n### 练习步骤：记录ABC、质疑B、建立新的B、观察C的变化",
                            "认知调整", "焦虑,悲伤,愤怒" },
                    { "运动减压方案", "通过身体活动释放压力激素",
                            "## 运动减压指南\n\n运动是天然的抗焦虑剂。\n\n### 推荐运动类型：\n- 有氧运动：快走、慢跑、跳绳、游泳\n- 瑜伽/拉伸：阴瑜伽、基础体式\n- 力量训练：俯卧撑、深蹲、平板支撑",
                            "运动调节", "焦虑,愤怒,恐惧" },
                    { "社交支持系统建设", "建立健康的人际关系网络",
                            "## 社交支持指南\n\n良好的社会支持是心理健康的保护伞。\n\n### 三层支持网络：\n- 核心圈（2-3人）：家人、挚友\n- 朋友圈（5-8人）：同事、同学\n- 泛社交圈：社群成员、网友",
                            "社会支持", "悲伤,焦虑,平静" }
            };

            int[] difficultyLevels = { 1, 1, 2, 3, 2, 2 };
            int[] durationMinutes = { 5, 15, 10, 20, 30, 25 };
            double[] ratings = { 5.0, 4.8, 4.9, 4.7, 4.6, 4.5 };

            for (int i = 0; i < solutions.length; i++) {
                jdbcTemplate.update(
                        "INSERT INTO solution (title, summary, content, category, target_emotion, difficulty_level, duration_minutes, author_id, status, use_count, like_count, rating, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, 1, 1, 0, 0, ?, NOW(), NOW())",
                        solutions[i][0], solutions[i][1], solutions[i][2], solutions[i][3], solutions[i][4],
                        difficultyLevels[i], durationMinutes[i], ratings[i]);
            }

            logger.info("方案库初始化完成，共插入 {} 条预设方案", solutions.length);
        } catch (Exception e) {
            logger.warn("方案库初始化失败: {}", e.getMessage());
        }
    }

    /**
     * 确保树洞收藏表存在
     */
    private void initFavoriteTable() {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS treehole_favorite (" +
                            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                            "content_id BIGINT NOT NULL, " +
                            "user_id BIGINT NOT NULL, " +
                            "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "UNIQUE KEY uk_content_user (content_id, user_id)" +
                            ") ENGINE=InnoDB COMMENT='树洞收藏表'");
            logger.info("树洞收藏表检查完成");
        } catch (Exception e) {
            logger.warn("树洞收藏表创建失败: {}", e.getMessage());
        }
    }
}
