CREATE TABLE mjga.aop_log (
    id BIGSERIAL PRIMARY KEY,
    class_name VARCHAR NOT NULL,
    method_name VARCHAR NOT NULL,
    method_args VARCHAR,
    return_value VARCHAR,
    execution_time BIGINT NOT NULL,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message VARCHAR,
    user_id BIGINT,
    ip_address VARCHAR,
    user_agent VARCHAR,
    create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES mjga.user(id) ON DELETE SET NULL
);

CREATE INDEX idx_aop_log_class_name ON mjga.aop_log(class_name);
CREATE INDEX idx_aop_log_method_name ON mjga.aop_log(method_name);
CREATE INDEX idx_aop_log_create_time ON mjga.aop_log(create_time);
CREATE INDEX idx_aop_log_user_id ON mjga.aop_log(user_id);
CREATE INDEX idx_aop_log_success ON mjga.aop_log(success);
