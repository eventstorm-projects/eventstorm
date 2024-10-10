package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class ItemBusinessKey extends Item {

    private final PojoDescriptor pojoDescriptor;
    private final List<Column> businessKeys;

    public ItemBusinessKey(String version, PojoDescriptor pojoDescriptor, List<Column> businessKeys) {
        super(version);
        this.pojoDescriptor = pojoDescriptor;
        this.businessKeys = businessKeys;
    }

    @Override
    public String toString() {
        return "UNIQUE INDEX[...]";
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE UNIQUE INDEX ");
        builder.append(pojoDescriptor.getTable().value());
        builder.append("_bk");
        builder.append(" ON ");
        builder.append(pojoDescriptor.getTable().value());
        builder.append("(");
        businessKeys.forEach(bk -> {
            builder.append(bk.value()).append(',');
        });
        builder.deleteCharAt(builder.length() - 1);
        builder.append(");\n");

        writer.write(builder.toString());

    }
}
