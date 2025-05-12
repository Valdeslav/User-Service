-- liquibase formatted sql
-- changeset Vladislav Tsarenko:2024-05-07_17-00_1
CREATE TABLE IF NOT EXISTS user_info (
    id        BIGSERIAL       PRIMARY KEY,
    username  VARCHAR(128)    UNIQUE NOT NULL,
    password  VARCHAR(60)     NOT NULL,
    email     VARCHAR(256)    UNIQUE
);

-- changeset Vladislav Tsarenko:2024-05-07_17-00_2
CREATE TABLE IF NOT EXISTS role (
    id    BIGSERIAL       PRIMARY KEY,
    name  VARCHAR(128)    UNIQUE NOT NULL
);

-- changeset Vladislav Tsarenko:2024-05-07_17-00_3
CREATE TABLE IF NOT EXISTS user_role (
    user_info_id    BIGINT  NOT NULL,
    role_id         BIGINT  NOT NULL,
    FOREIGN KEY (user_info_id) REFERENCES user_info(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- changeset Vladislav Tsarenko:2024-05-07_17-00_4
CREATE TABLE IF NOT EXISTS refresh_token (
    id              BIGSERIAL       PRIMARY KEY,
    token_value     VARCHAR(256)    NOT NULL,
    expiry_date     TIMESTAMP       NOT NULL,
    user_info_id    BIGINT          NOT NULL,
    FOREIGN KEY (user_info_id) REFERENCES user_info(id)
);