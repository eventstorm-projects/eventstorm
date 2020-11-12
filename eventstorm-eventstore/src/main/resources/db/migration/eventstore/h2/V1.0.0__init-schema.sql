CREATE TABLE "event_store" (
   "stream"                 VARCHAR(64)  	NOT NULL,
   "stream_id"              VARCHAR(64)   	NOT NULL,
   "revision"               INT            	NOT NULL,
   "time"                   TIMESTAMP WITH TIME ZONE NOT NULL,
   "event_type"             VARCHAR(128)   	NOT NULL,
   "correlation"            VARCHAR(36)   	NOT NULL,
   "payload"                TEXT		   	NOT NULL,
   PRIMARY KEY ("stream","stream_id","revision")
);

CREATE SEQUENCE "seq__event_store";

CREATE INDEX "idx__event_store" ON "event_store"("stream","stream_id");
