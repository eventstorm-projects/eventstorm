package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.UpdateTimestamp;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;

import java.io.IOException;
import java.io.Writer;

public class ItemTable extends Item {

    private final PojoDescriptor descriptor;

    public ItemTable(String version, PojoDescriptor pojoDescriptor) {
        super(version);
        this.descriptor = pojoDescriptor;
    }

    @Override
    public String toString() {
        return "TABLE_[" + descriptor.getTable().value() + "]";
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {

        Table table = descriptor.getTable();

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(dialect.wrap(table.value()));
        builder.append(" (");

        StringBuilder primaryKey = new StringBuilder();
        primaryKey.append("PRIMARY KEY (");

        if (descriptor.ids().size() > 0) {
            for (PojoPropertyDescriptor id : descriptor.ids()) {

                builder.append("\n   ");
                PrimaryKey anno = id.getter().getAnnotation(PrimaryKey.class);
                String columnName = anno.value();
                builder.append(dialect.wrap(columnName));
                for (int i = columnName.length(); i < 24; i++) {
                    builder.append(' ');
                }

                AutoIncrement autoIncrement = id.getter().getAnnotation(AutoIncrement.class);
                if (autoIncrement != null) {
                    builder.append(dialect.autoIncrementType(id.getter().getReturnType().toString()));
                } else {
                    builder.append(dialect.toSqlType(id.getter().getReturnType().toString(), anno));
                }
                builder.append(",");

                primaryKey.append(dialect.wrap(anno.value())).append(',');

            }
            primaryKey.deleteCharAt(primaryKey.length() - 1).append(')');
        }

        if (descriptor.properties().size() > 0) {
            for (PojoPropertyDescriptor col : descriptor.properties()) {
                builder.append("\n   ");
                String columnName = Helper.getSqlColumnName(col);
                builder.append(dialect.wrap(columnName));
                for (int i = columnName.length(); i < 24; i++) {
                    builder.append(' ');
                }
                if (columnName.length() >= 24) {
                    builder.append(' ');
                }

                Column anno = col.getter().getAnnotation(Column.class);
                String type = dialect.toSqlType(col.getter().getReturnType().toString(), anno);
                builder.append(type);
                for (int i = type.length(); i < 16; i++) {
                    builder.append(' ');
                }

                if (!anno.nullable() && col.getter().getAnnotation(UpdateTimestamp.class) == null) {
                    builder.append(" NOT NULL");
                }

                builder.append(",");

            }
        }
        builder.deleteCharAt(builder.length() - 1);

        if (descriptor.ids().size() > 0) {
            builder.append(",\n   ");
            builder.append(primaryKey);
            builder.deleteCharAt(builder.length() - 1);
            builder.append(')');
        }

        builder.append("\n);\n");

        writer.append(builder.toString());
        writer.flush();

    }
}
