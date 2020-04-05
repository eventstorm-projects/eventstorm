CREATE TABLE "event_store" (
   "id"                     BIGINT,
   "aggregate_type"         VARCHAR(128)   NOT NULL,
   "aggregate_id"           VARCHAR(128)   NOT NULL,
   "revision"               INT            NOT NULL,
   "time"                   TIMESTAMP WITH TIME ZONE NOT NULL,
   "payload_type"           VARCHAR(128)   NOT NULL,
   "payload"                BLOB		   NOT NULL,
   PRIMARY KEY ("id"),
   UNIQUE ("aggregate_type","aggregate_id","revision")
);

CREATE TABLE "event_manager" (
   "id"                     INT,
   "aggregate_type"         VARCHAR(128)   NOT NULL,
   PRIMARY KEY ("id"),
   UNIQUE ("aggregate_type")
);

CREATE TABLE "event_definition" (
   "id"                     INT,
   "version"                INT            NOT NULL,
   PRIMARY KEY ("id", "version")
);

CREATE SEQUENCE "seq__event_store";

CREATE INDEX "idx__event_store" ON "event_store"("aggregate_type","aggregate_id");
