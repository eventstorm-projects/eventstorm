CREATE TABLE "batch_resource" (
   -- PRIMARY KEY
   "id"                      VARCHAR(36),
   -- COLUMNS
   "meta"                    JSON             NOT NULL,
   "content"                 BLOB             NOT NULL,
   "created_by"              VARCHAR(64)      NOT NULL,
   "created_at"              TIMESTAMP        NOT NULL,
   PRIMARY KEY ("id")
);
CREATE TABLE "batch_execution" (
   -- PRIMARY KEY
   "uuid"                    VARCHAR(36),
   -- COLUMNS
   "event"                   VARCHAR(4000)    NOT NULL,
   "name"                    VARCHAR(128)     NOT NULL,
   "status"                  VARCHAR(9)       NOT NULL,
   "created_by"              VARCHAR(64)      NOT NULL,
   "created_at"              TIMESTAMP        NOT NULL,
   "started_at"              TIMESTAMP       ,
   "ended_at"                TIMESTAMP       ,
   "log"                     JSON             NOT NULL,
   PRIMARY KEY ("uuid")
);
