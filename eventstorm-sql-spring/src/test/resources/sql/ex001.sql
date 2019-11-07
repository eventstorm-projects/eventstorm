CREATE TABLE "student" (
   "id"   INT,
   "code"                    VARCHAR(255)     NOT NULL,
   "age"                     INT              NOT NULL,
   "overall_rating"          BIGINT          ,
   "created_at"              TIMESTAMP        NOT NULL,
   "readonly"                VARCHAR(255)    ,
   PRIMARY KEY ("id")
);

CREATE INDEX student_bk ON "student"("code");