package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

abstract class UpdateColumn extends SqlColumn {

    UpdateColumn(SqlSingleColumn column) {
        super(column.table(), column.name(), column.alias());
    }

    @Override
    public SqlColumn as(String alias) {
        throw new UnsupportedOperationException();

    }

    @Override
    public SqlColumn newColumnFromAlias(SqlTable targetTable) {
        throw new UnsupportedOperationException();
    }

    abstract String updateSql(Dialect dialect);

}
