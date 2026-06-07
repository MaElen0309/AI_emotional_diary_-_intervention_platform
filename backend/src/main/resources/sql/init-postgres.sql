-- AI情绪日记与干预平台 - PostgreSQL 初始化脚本（Render 云平台）
-- 兼容 PostgreSQL 语法

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    role SMALLINT NOT NULL DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    student_id VARCHAR(20),
    department VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 情绪日记表
CREATE TABLE IF NOT EXISTS emotion_diary (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    emotion_type SMALLINT,
    emotion_score INT,
    ai_analysis_result JSONB,
    risk_level SMALLINT DEFAULT 0,
    is_private SMALLINT DEFAULT 1,
    like_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_diary_user ON emotion_diary(user_id);
CREATE INDEX IF NOT EXISTS idx_diary_time ON emotion_diary(create_time);

-- 树洞内容表
CREATE TABLE IF NOT EXISTS treehole_content (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    anonymous_name VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category_id BIGINT,
    images JSONB,
    audit_status SMALLINT DEFAULT 0,
    audit_user_id BIGINT,
    audit_remark VARCHAR(500),
    audit_time TIMESTAMP,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    is_top SMALLINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_treehole_audit ON treehole_content(audit_status);
CREATE INDEX IF NOT EXISTS idx_treehole_cat ON treehole_content(category_id);

-- 树洞分类表
CREATE TABLE IF NOT EXISTS treehole_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 树洞评论表
CREATE TABLE IF NOT EXISTS treehole_comment (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT,
    anonymous_name VARCHAR(50),
    content TEXT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    like_count INT DEFAULT 0,
    audit_status SMALLINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_comment_content ON treehole_comment(content_id);

-- 心理调节方案表
CREATE TABLE IF NOT EXISTS solution (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content TEXT NOT NULL,
    cover_image VARCHAR(255),
    audio_url VARCHAR(255),
    category VARCHAR(50),
    target_emotion VARCHAR(100),
    difficulty_level SMALLINT,
    duration_minutes INT,
    author_id BIGINT,
    status SMALLINT DEFAULT 0,
    use_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    rating DECIMAL(2,1) DEFAULT 5.0,
    publish_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_solution_status ON solution(status);

-- 方案使用记录表
CREATE TABLE IF NOT EXISTS solution_usage_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    solution_id BIGINT NOT NULL,
    usage_duration INT,
    feedback_text TEXT,
    feedback_rating SMALLINT,
    is_helpful SMALLINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_usage_user ON solution_usage_record(user_id);
CREATE INDEX IF NOT EXISTS idx_usage_solution ON solution_usage_record(solution_id);

-- 系统参数配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_type VARCHAR(50) DEFAULT 'string',
    description VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(50) NOT NULL,
    method VARCHAR(200),
    params TEXT,
    ip VARCHAR(50),
    url VARCHAR(255),
    status SMALLINT DEFAULT 1,
    error_msg TEXT,
    execute_time BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_oplog_user ON operation_log(user_id);
CREATE INDEX IF NOT EXISTS idx_oplog_operation ON operation_log(operation);
CREATE INDEX IF NOT EXISTS idx_oplog_time ON operation_log(create_time);

-- AI分析记录表
CREATE TABLE IF NOT EXISTS ai_analysis_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    input_content TEXT,
    output_result JSONB,
    model_version VARCHAR(50),
    tokens_used INT,
    execution_ms INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ai_user ON ai_analysis_record(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_target ON ai_analysis_record(target_type, target_id);

-- 插入初始数据（默认密码: 123456 SHA-256）
INSERT INTO sys_user (username, password, real_name, nickname, role, status)
SELECT 'admin', '4XrrgwgFzse8X1mXtMUtQr0Sz40rDSERj61FmHCSWwg=', '系统管理员', '管理员', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user (username, password, real_name, nickname, role, status)
SELECT 'teacher01', '4XrrgwgFzse8X1mXtMUtQr0Sz40rDSERj61FmHCSWwg=', '张老师', '张老师', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'teacher01');

-- 插入树洞分类
INSERT INTO treehole_category (name, description, sort_order)
SELECT '情感倾诉', '分享情感故事和经历', 1
WHERE NOT EXISTS (SELECT 1 FROM treehole_category WHERE name = '情感倾诉');

INSERT INTO treehole_category (name, description, sort_order)
SELECT '学业压力', '讨论学习压力和困惑', 2
WHERE NOT EXISTS (SELECT 1 FROM treehole_category WHERE name = '学业压力');

INSERT INTO treehole_category (name, description, sort_order)
SELECT '人际关系', '交流人际交往问题', 3
WHERE NOT EXISTS (SELECT 1 FROM treehole_category WHERE name = '人际关系');

INSERT INTO treehole_category (name, description, sort_order)
SELECT '生活琐事', '记录日常生活点滴', 4
WHERE NOT EXISTS (SELECT 1 FROM treehole_category WHERE name = '生活琐事');

INSERT INTO treehole_category (name, description, sort_order)
SELECT '其他', '其他话题', 5
WHERE NOT EXISTS (SELECT 1 FROM treehole_category WHERE name = '其他');

-- 插入系统配置
INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'site.name', 'AI情绪日记与干预平台', 'string', '网站名称'
WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'site.name');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'ai.auto_analyze', 'true', 'boolean', '是否自动进行AI分析'
WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'ai.auto_analyze');

INSERT INTO system_config (config_key, config_value, config_type, description)
SELECT 'audit.treehole_auto', 'false', 'boolean', '树洞是否自动通过审核'
WHERE NOT EXISTS (SELECT 1 FROM system_config WHERE config_key = 'audit.treehole_auto');
