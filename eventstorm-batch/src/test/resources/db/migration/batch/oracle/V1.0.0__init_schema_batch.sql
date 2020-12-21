CREATE TABLE batch_resource (
   -- PRIMARY KEY
   id                      VARCHAR2(36),
   -- COLUMNS
   meta                    BLOB             NOT NULL,
   content                 BLOB             NOT NULL,
   created_by              VARCHAR2(64)     NOT NULL,
   created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
   PRIMARY KEY (id)
);
CREATE TABLE batch_execution (
   -- PRIMARY KEY
   uuid                    VARCHAR2(36),
   -- COLUMNS
   event                   VARCHAR2(4000)   NOT NULL,
   name                    VARCHAR2(128)    NOT NULL,
   status                  NUMBER(3)        NOT NULL,
   created_by              VARCHAR2(64)     NOT NULL,
   created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
   started_at              TIMESTAMP WITH TIME ZONE,
   ended_at                TIMESTAMP WITH TIME ZONE,
   log                     VARCHAR2(255)    NOT NULL,
   PRIMARY KEY (uuid)
);
