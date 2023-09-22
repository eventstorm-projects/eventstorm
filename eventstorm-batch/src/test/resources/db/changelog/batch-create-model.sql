-- liquibase formatted sql

-- changeset eventstorm:1.0.0 dbms:h2
CREATE TABLE batch_resource (
    id              VARCHAR(36),
    meta            JSON             NOT NULL,
    content         BLOB             NOT NULL,
    created_by      VARCHAR(64)      NOT NULL,
    created_at      TIMESTAMP        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE batch_execution (
    uuid        VARCHAR(36),
    event       VARCHAR(4000)    NOT NULL,
    name        VARCHAR(128)     NOT NULL,
    status      VARCHAR(9)       NOT NULL,
    created_by  VARCHAR(64)      NOT NULL,
    created_at  TIMESTAMP        NOT NULL,
    started_at  TIMESTAMP       ,
    ended_at    TIMESTAMP       ,
    log         JSON             NOT NULL,
    PRIMARY KEY (uuid)
);

-- changeset eventstorm:1.0.0 dbms:oracle
CREATE TABLE batch_resource (
    id              VARCHAR2(36),
    meta            BLOB             NOT NULL,
    content         BLOB             NOT NULL,
    created_by      VARCHAR2(64)     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE batch_execution (
    uuid        VARCHAR2(36),
    event       VARCHAR2(4000)   NOT NULL,
    name        VARCHAR2(128)    NOT NULL,
    status      NUMBER(3)        NOT NULL,
    created_by  VARCHAR2(64)     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at  TIMESTAMP WITH TIME ZONE,
    ended_at    TIMESTAMP WITH TIME ZONE,
    log         BLOB    NOT NULL,
    PRIMARY KEY (uuid)
);
