package eu.eventstorm.sql.impl;


import eu.eventstorm.sql.Descriptor;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseSchemaChecker {

    private DatabaseSchemaChecker() {
    }


    static void checkModule(StringBuilder builder, DatabaseMetaData meta, Module module) throws SQLException {

        builder.append("\nModule   -> name : [").append(module.name()).append(']');
        for (Descriptor descriptor : module.descriptors()) {
            builder.append("\n         \t-> table : [").append(module.getTableName(descriptor.table())).append("] -> alias [").append(descriptor.table().alias()).append("] -> exists [");
            builder.append(checkTable(meta, module, descriptor.table())).append(']');

            for (SqlPrimaryKey key : descriptor.ids()) {
                builder.append("\n         \t\t-> id : [").append(key.name()).append("] -> exists [");
                builder.append(checkColumn(meta, module, descriptor.table(), key)).append(']');
            }

            for (SqlSingleColumn column : descriptor.columns()) {
                builder.append("\n         \t\t-> column : [").append(column.name()).append("] -> exists [");
                builder.append(checkColumn(meta, module, descriptor.table(), column)).append(']');
            }
        }

    }

    private static boolean checkColumn(DatabaseMetaData meta, Module module, SqlTable table, SqlColumn column) throws SQLException {
        try (ResultSet res = meta.getColumns(null, null, module.getTableName(table).toUpperCase(), column.name().toUpperCase())) {
            return res.next();
        }
    }

    private static boolean checkTable(DatabaseMetaData meta, Module module, SqlTable table) throws SQLException {
        String name = module.getTableName(table).toUpperCase();
        try (ResultSet res = meta.getTables(null, null, null, new String[]{"TABLE"})) {
            while (res.next()) {
                if (name.equals(res.getString("TABLE_NAME").toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
    }
}
