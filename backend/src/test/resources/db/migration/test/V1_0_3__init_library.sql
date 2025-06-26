CREATE TABLE mjga.library (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR NOT NULL UNIQUE,
                              data_count INTEGER NOT NULL DEFAULT 0,
                              create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mjga.library_doc (
                                  id BIGSERIAL PRIMARY KEY,
                                  lib_id BIGINT NOT NULL,
                                  name VARCHAR NOT NULL,
                                  identify VARCHAR NOT NULL UNIQUE,
                                  path VARCHAR NOT NULL,
                                  meta JSON NOT NULL,
                                  enable BOOLEAN NOT NULL DEFAULT true,
                                  create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  update_time TIMESTAMPTZ,
                                  FOREIGN KEY (lib_id) REFERENCES mjga.library (id) ON DELETE CASCADE
);

CREATE TABLE mjga.library_doc_segment (
                                          id BIGSERIAL PRIMARY KEY,
                                          doc_id BIGINT NOT NULL,
                                          embedding_id VARCHAR NOT NULL UNIQUE,
                                          content TEXT,
                                          token_usage INTEGER NOT NULL DEFAULT 0,
                                          FOREIGN KEY (doc_id) REFERENCES mjga.library_doc (id) ON DELETE CASCADE
);