INSERT INTO mjga.user (username, password)
VALUES ('admin', '$2a$10$7zfEdqQYJrBnmDdu7UkgS.zOAsJf4bB1ZYrVhCBAIvIoPbEmeVnVe');

INSERT INTO mjga.role (code, name)
VALUES ('ADMIN', 'ADMIN'),
       ('GENERAL', 'GENERAL');

INSERT INTO mjga.permission (code, name)
VALUES   ('READ_POSITION_PERMISSION', 'READ_POSITION_PERMISSION'),
         ('WRITE_POSITION_PERMISSION', 'WRITE_POSITION_PERMISSION'),
         ('READ_DEPARTMENT_PERMISSION', 'READ_DEPARTMENT_PERMISSION'),
         ('WRITE_DEPARTMENT_PERMISSION', 'WRITE_DEPARTMENT_PERMISSION'),
         ('READ_SCHEDULER_PERMISSION', 'READ_SCHEDULER_PERMISSION'),
         ('WRITE_SCHEDULER_PERMISSION', 'WRITE_SCHEDULER_PERMISSION'),
         ('WRITE_USER_ROLE_PERMISSION', 'WRITE_USER_ROLE_PERMISSION'),
         ('READ_USER_ROLE_PERMISSION', 'READ_USER_ROLE_PERMISSION');

INSERT INTO mjga.user_role_map (user_id, role_id)
VALUES (1, 1);

INSERT INTO mjga.role_permission_map (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8);

INSERT INTO mjga.ai_llm_config (name,code,model_name, api_key, url, enable, priority)
VALUES
    ('DeepSeek','DEEP_SEEK','deepseek-chat','your_api_key', 'https://api.deepseek.com', false, 0),
    ('智谱清言','ZHI_PU','glm-4-flash', 'your_api_key', 'https://open.bigmodel.cn/', false, 1);