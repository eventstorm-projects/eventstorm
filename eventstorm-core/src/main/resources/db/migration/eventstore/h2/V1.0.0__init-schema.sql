CREATE TABLE "event_store" (
   "id"                BIGINT,
   "aggregate_type"    VARCHAR(128)   NOT NULL,
   "aggregate_id"      VARCHAR(128)   NOT NULL,
   "revision"          INT            NOT NULL,
   "time"              TIMESTAMP WITH TIME ZONE NOT NULL,
   PRIMARY KEY ("id")
);

CREATE SEQUENCE "seq__event_store";

CREATE INDEX "idx__event_store" ON "event_store"("aggregate_type","aggregate_id");