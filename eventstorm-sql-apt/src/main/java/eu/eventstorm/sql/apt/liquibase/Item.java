package eu.eventstorm.sql.apt.liquibase;

import java.io.IOException;
import java.io.Writer;

abstract class Item {

    private final String version;

    Item(String version) {
        this.version = version;
    }

    public final String getVersion() {
        return version;
    }

    abstract void write(Writer writer, DatabaseDialect dialect) throws IOException;

}
