CREATE TABLE "event_store" (
   "id"                     BIGINT,
   "stream"                 VARCHAR(128)   NOT NULL,
   "stream_id"              VARCHAR(128)   NOT NULL,
   "revision"               INT            NOT NULL,
   "time"                   TIMESTAMP WITH TIME ZONE NOT NULL,
   "event_type"             VARCHAR(128)   NOT NULL,
   "payload"                BLOB		   NOT NULL,
   PRIMARY KEY ("id"),
   UNIQUE ("stream","stream_id","revision")
);

CREATE SEQUENCE "seq__event_store";

CREATE INDEX "idx__event_store" ON "event_store"("stream","stream_id");
