package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Index;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

import java.io.IOException;
import java.io.Writer;

public class ItemIndex extends Item {

    private final PojoDescriptor pojoDescriptor;
    private final Index index;

    public ItemIndex(String version, PojoDescriptor pojoDescriptor, Index index) {
        super(version);
        this.pojoDescriptor = pojoDescriptor;
        this.index = index;
    }

    @Override
    public String toString() {
        return "INDEX[" + index.name() + "]->TABLE[" + pojoDescriptor.getTable().value()+"]";
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE ");
        if (index.unique()) {
            builder.append("UNIQUE ");
        }
        builder.append("INDEX ");
        builder.append(index.name());
        builder.append(" ON ");
        builder.append(pojoDescriptor.getTable().value());
        builder.append("(");
        for (String column : index.columns()) {
            builder.append(column).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(");\n");

        writer.append(builder.toString());

    }
}
