package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.apt.log.Logger;

import java.io.IOException;
import java.io.Writer;

class DeferWriter {

    private final Logger logger;
    private final Writer writer;
    private final String ddl;

    DeferWriter(Logger logger, Writer writer, String ddl) {
        this.logger = logger;
        this.writer = writer;
        this.ddl = ddl;
    }

    void write() {
        try {
            this.writer.write(ddl);
        } catch (IOException cause) {
            logger.error("failed to writer defer ddl", cause);
        }

    }

}