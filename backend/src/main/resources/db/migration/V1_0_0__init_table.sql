CREATE SCHEMA IF NOT EXISTS mjga;

CREATE TABLE mjga.user (
                           id BIGSERIAL PRIMARY KEY,
                           username VARCHAR NOT NULL UNIQUE,
                           avatar VARCHAR,
                           create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           password VARCHAR NOT NULL,
                           enable BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE mjga.permission (
                                 id BIGSERIAL PRIMARY KEY,
                                 code VARCHAR NOT NULL UNIQUE,
                                 name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE mjga.role (
                           id BIGSERIAL PRIMARY KEY,
                           code VARCHAR NOT NULL UNIQUE,
                           name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE mjga.role_permission_map (
                                          role_id BIGINT NOT NULL,
                                          permission_id BIGINT NOT NULL,
                                          PRIMARY KEY (role_id, permission_id),
                                          FOREIGN KEY (role_id) REFERENCES mjga.role(id) ON DELETE RESTRICT,
                                          FOREIGN KEY (permission_id) REFERENCES mjga.permission(id) ON DELETE RESTRICT
);

CREATE TABLE mjga.user_role_map (
                                    user_id BIGINT NOT NULL,
                                    role_id BIGINT NOT NULL,
                                    PRIMARY KEY (user_id, role_id),
                                    FOREIGN KEY (user_id) REFERENCES mjga.user(id) ON DELETE RESTRICT,
                                    FOREIGN KEY (role_id) REFERENCES mjga.role(id) ON DELETE RESTRICT
);

CREATE TABLE mjga.department (
                                 id BIGSERIAL PRIMARY KEY,
                                 name VARCHAR NOT NULL UNIQUE,
                                 parent_id BIGINT,
                                 FOREIGN KEY (parent_id)
                                     REFERENCES mjga.department(id)
                                     ON DELETE RESTRICT
);

CREATE TABLE mjga.user_department_map (
                                          user_id BIGINT NOT NULL,
                                          department_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, department_id),
                                          FOREIGN KEY (user_id) REFERENCES mjga.user(id) ON UPDATE NO ACTION ON DELETE RESTRICT,
                                          FOREIGN KEY (department_id) REFERENCES mjga.department(id) ON UPDATE NO ACTION ON DELETE RESTRICT
);

CREATE TABLE mjga.position (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE mjga.user_position_map (
                                        user_id BIGINT NOT NULL,
                                        position_id BIGINT NOT NULL,
                                        PRIMARY KEY (user_id, position_id),
                                        FOREIGN KEY (user_id) REFERENCES mjga.user(id) ON UPDATE NO ACTION ON DELETE RESTRICT,
                                        FOREIGN KEY (position_id) REFERENCES mjga.position(id) ON UPDATE NO ACTION ON DELETE RESTRICT
);

CREATE TYPE mjga.llm_code_enum AS ENUM (
	'DEEP_SEEK',
	'ZHI_PU',
    'ZHI_PU_EMBEDDING'
);

CREATE TYPE "llm_type_enum" AS ENUM (
	'CHAT',
	'EMBEDDING'
);

CREATE TABLE mjga.ai_llm_config (
                                 id BIGSERIAL NOT NULL UNIQUE,
                                 name VARCHAR NOT NULL UNIQUE,
                                 code mjga.llm_code_enum NOT NULL UNIQUE,
                                 model_name VARCHAR NOT NULL,
                                 type LLM_TYPE_ENUM NOT NULL,
                                 api_key VARCHAR NOT NULL,
                                 url VARCHAR NOT NULL,
                                 enable BOOLEAN NOT NULL DEFAULT true,
                                 priority SMALLINT NOT NULL DEFAULT 0,
                                 PRIMARY KEY(id)
);
