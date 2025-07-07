-- AOP日志表
CREATE TABLE mjga.aop_log (
    id BIGSERIAL PRIMARY KEY,
    class_name VARCHAR NOT NULL,
    method_name VARCHAR NOT NULL,
    method_args TEXT,
    return_value TEXT,
    execution_time BIGINT NOT NULL,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message TEXT,
    user_id BIGINT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES mjga.user(id) ON DELETE SET NULL
);

-- 创建索引以提高查询性能
CREATE INDEX idx_aop_log_class_name ON mjga.aop_log(class_name);
CREATE INDEX idx_aop_log_method_name ON mjga.aop_log(method_name);
CREATE INDEX idx_aop_log_create_time ON mjga.aop_log(create_time);
CREATE INDEX idx_aop_log_user_id ON mjga.aop_log(user_id);
CREATE INDEX idx_aop_log_success ON mjga.aop_log(success);
