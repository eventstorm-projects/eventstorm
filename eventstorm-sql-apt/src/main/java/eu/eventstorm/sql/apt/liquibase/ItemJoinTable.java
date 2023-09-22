package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.JoinColumn;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.model.PojoPropertyDescriptor;

import java.io.IOException;
import java.io.Writer;

public class ItemJoinTable extends Item {

    private final PojoDescriptor descriptor;

    public ItemJoinTable(String version, PojoDescriptor pojoDescriptor) {
        super(version);
        this.descriptor = pojoDescriptor;
    }

    @Override
    public String toString() {
        return "JOINTABLE_[" + descriptor.getTable().value() + "]";
    }

    @Override
    void write(Writer writer, DatabaseDialect dialect) throws IOException {

        JoinTable table = descriptor.getJoinTable();


        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(dialect.wrap(table.value()));
        builder.append(" (");

        StringBuilder primaryKey = new StringBuilder();
        primaryKey.append("PRIMARY KEY (");

        for (PojoPropertyDescriptor id : descriptor.ids()) {
            builder.append("\n   ");
            JoinColumn anno = id.getter().getAnnotation(JoinColumn.class);
            builder.append(dialect.wrap(anno.value()));
            builder.append("   ");

            // TODO search joinColumn to id target;
            //builder.append(fd.toSqlType(id.getter().getReturnType().toString(), id.getter().getAnnotation(JoinColumn.class)));
            builder.append(" INT");
            builder.append(",");

            primaryKey.append(dialect.wrap(anno.value())).append(",");

        }
        primaryKey.deleteCharAt(primaryKey.length() - 1).append("),");

        for (PojoPropertyDescriptor col : descriptor.properties()) {
            builder.append("\n   ");

            String columnName = Helper.getSqlColumnName(col);
            builder.append(dialect.wrap(columnName));
            for (int i = columnName.length(); i < 24; i++) {
                builder.append(' ');
            }
            String type = dialect.toSqlType(col.getter().getReturnType().toString(), col.getter().getAnnotation(Column.class));
            builder.append(type);
            for (int i = type.length(); i < 16; i++) {
                builder.append(' ');
            }
            if (!col.getter().getAnnotation(Column.class).nullable()) {
                builder.append(" NOT NULL");
            }

            builder.append(",");

        }

        builder.append("\n   ");
        builder.append(primaryKey);

        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n);\n");

        writer.append(builder.toString());
    }
}
