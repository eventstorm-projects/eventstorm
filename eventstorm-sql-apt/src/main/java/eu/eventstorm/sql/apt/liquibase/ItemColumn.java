package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

import java.io.Writer;

public class ItemColumn extends Item {

    private final PojoDescriptor pojoDescriptor;
    private final Column column;

    public ItemColumn(String version, PojoDescriptor pojoDescriptor, Column column) {
        super(version);
        this.pojoDescriptor = pojoDescriptor;
        this.column = column;
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) {

    }
}
