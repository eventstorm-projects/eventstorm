package eu.eventstorm.sql.builder;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.Expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class UpdateBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateBuilder.class);

    private final Database database;
    private final SqlTable table;
    private final ImmutableList<SqlSingleColumn> columns;
    private final ImmutableList<SqlPrimaryKey> keys;
    private Expression where;

    public UpdateBuilder(Database database, SqlTable table, ImmutableList<SqlSingleColumn> columns, ImmutableList<SqlPrimaryKey> keys) {
        super(database);
        this.database = database;
        this.table = table;
        this.columns = columns;
        this.keys = keys;
    }

    public UpdateBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public SqlQuery build() {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("UPDATE ");
        builder.append(table(this.table, false));
        builderSetValues(builder, this.database.dialect());
        builder.append(" WHERE ");

        if (this.where == null) {
            builderWherePk(builder, this.database.dialect());
        } else {
            builder.append(where.build(database.dialect(), false));
        }

        String sql = builder.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }

        return new SqlQueryImpl(sql);
    }

    private void builderSetValues(StringBuilder builder, Dialect dialect) {
        builder.append(" SET ");
        for (SqlSingleColumn column : this.columns) {
            if (column.isUpdatable()) {
                dialect.wrap(builder, column, false);
                builder.append("=?,");
            }

        }
        builder.setLength(builder.length() - 1);
    }

    private void builderWherePk(StringBuilder builder, Dialect dialect) {
        for (SqlPrimaryKey column : this.keys) {
            dialect.wrap(builder, column, false);
            builder.append("=? AND ");
        }
        builder.setLength(builder.length() - 5);
    }

}