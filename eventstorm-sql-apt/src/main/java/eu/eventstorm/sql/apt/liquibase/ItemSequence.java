package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.apt.log.Logger;

import java.io.IOException;
import java.io.Writer;

public class ItemSequence extends Item {

    private final Sequence sequence;
    private final Logger logger;

    public ItemSequence(Logger logger, String version, Sequence sequence) {
        super(version);
        this.logger = logger;
        this.sequence = sequence;
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {
        logger.info("generate sequence " + sequence);
        writer.append("CREATE SEQUENCE " + dialect.wrap(sequence.value())  + ";\n");
    }

}