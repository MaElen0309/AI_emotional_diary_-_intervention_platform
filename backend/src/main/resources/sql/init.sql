-- AI情绪日记与干预平台数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS emotional_diary DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE emotional_diary;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-学生，1-心理老师，2-系统管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    student_id VARCHAR(20) COMMENT '学号（学生角色必填）',
    department VARCHAR(100) COMMENT '院系',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='用户信息表';

-- 情绪日记表
CREATE TABLE emotion_diary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    emotion_type TINYINT COMMENT '情绪类型：1-开心，2-平静，3-焦虑，4-悲伤，5-愤怒，6-恐惧，7-其他',
    emotion_score INT COMMENT '情绪强度评分（1-10）',
    ai_analysis_result JSON COMMENT 'AI分析结果（JSON格式）',
    risk_level TINYINT COMMENT '风险等级：0-正常，1-低风险，2-中风险，3-高风险',
    is_private TINYINT DEFAULT 1 COMMENT '是否私密：0-公开，1-私密',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='情绪日记表';

-- 树洞内容表
CREATE TABLE treehole_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '树洞ID',
    user_id BIGINT COMMENT '发布者ID（可为空表示完全匿名）',
    anonymous_name VARCHAR(50) COMMENT '匿名昵称',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    category_id BIGINT COMMENT '分类ID',
    images JSON COMMENT '图片URL列表（JSON数组）',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
    audit_user_id BIGINT COMMENT '审核人ID',
    audit_remark VARCHAR(500) COMMENT '审核备注',
    audit_time DATETIME COMMENT '审核时间',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    view_count INT DEFAULT 0 COMMENT '浏览数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_audit_status (audit_status),
    INDEX idx_category_id (category_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='树洞内容表';

-- 树洞分类表
CREATE TABLE treehole_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='树洞分类表';

-- 树洞评论表
CREATE TABLE treehole_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    content_id BIGINT NOT NULL COMMENT '树洞内容ID',
    user_id BIGINT COMMENT '评论者ID（可为空）',
    anonymous_name VARCHAR(50) COMMENT '匿名昵称',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID（0表示顶级评论）',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_content_id (content_id),
    FOREIGN KEY (content_id) REFERENCES treehole_content(id)
) ENGINE=InnoDB COMMENT='树洞评论表';

-- 心理调节方案表
CREATE TABLE solution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '方案ID',
    title VARCHAR(200) NOT NULL COMMENT '方案标题',
    summary VARCHAR(500) COMMENT '方案摘要',
    content LONGTEXT NOT NULL COMMENT '方案详细内容',
    cover_image VARCHAR(255) COMMENT '封面图URL',
    audio_url VARCHAR(255) COMMENT '音频资源URL',
    category VARCHAR(50) COMMENT '方案分类',
    target_emotion VARCHAR(100) COMMENT '适用情绪类型',
    difficulty_level TINYINT COMMENT '难度等级：1-初级，2-中级，3-高级',
    duration_minutes INT COMMENT '建议时长（分钟）',
    author_id BIGINT COMMENT '作者ID（心理老师）',
    status TINYINT DEFAULT 0 COMMENT '上架状态：0-草稿，1-已上架，2-已下架',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    rating DECIMAL(2,1) DEFAULT 5.0 COMMENT '平均评分',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_category (category),
    FOREIGN KEY (author_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='心理调节方案表';

-- 方案使用记录表
CREATE TABLE solution_usage_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    solution_id BIGINT NOT NULL COMMENT '方案ID',
    usage_duration INT COMMENT '使用时长（秒）',
    feedback_text TEXT COMMENT '反馈文本',
    feedback_rating TINYINT COMMENT '反馈评分（1-5）',
    is_helpful TINYINT COMMENT '是否有帮助：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
    INDEX idx_user_id (user_id),
    INDEX idx_solution_id (solution_id)
) ENGINE=InnoDB COMMENT='方案使用记录表';

-- 系统参数配置表
CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT 'string' COMMENT '值类型：string,number,boolean,json',
    description VARCHAR(255) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统参数配置表';

-- 操作日志表
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    url VARCHAR(255) COMMENT '请求URL',
    status TINYINT DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    execute_time BIGINT COMMENT '执行时长（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation (operation),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB COMMENT='操作日志表';

-- AI分析记录表
CREATE TABLE ai_analysis_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：diary,treehole',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    analysis_type VARCHAR(50) NOT NULL COMMENT '分析类型：emotion,risk,solution',
    input_content TEXT COMMENT '输入内容',
    output_result JSON COMMENT 'AI返回结果（JSON格式）',
    model_version VARCHAR(50) COMMENT '模型版本',
    tokens_used INT COMMENT '消耗token数',
    execution_ms INT COMMENT '执行耗时（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析时间',
    INDEX idx_user_id (user_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB COMMENT='AI分析记录表';

-- 插入初始数据
INSERT INTO sys_user (username, password, real_name, nickname, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '管理员', 2, 1),
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张老师', '张老师', 1, 1);

INSERT INTO treehole_category (name, description, sort_order) VALUES
('情感倾诉', '分享情感故事和经历', 1),
('学业压力', '讨论学习压力和困惑', 2),
('人际关系', '交流人际交往问题', 3),
('生活琐事', '记录日常生活点滴', 4),
('其他', '其他话题', 5);

INSERT INTO system_config (config_key, config_value, config_type, description) VALUES
('site.name', 'AI情绪日记与干预平台', 'string', '网站名称'),
('ai.auto_analyze', 'true', 'boolean', '是否自动进行AI分析'),
('audit.treehole_auto', 'false', 'boolean', '树洞是否自动通过审核'),
('session.timeout', '1800', 'number', 'Session超时时间（秒）');
