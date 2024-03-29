package eu.eventstorm.sql.builder;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.DerivedColumn;
import eu.eventstorm.sql.desc.InsertableSqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.AggregateFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InsertBuilder extends AbstractBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertBuilder.class);

    private final SqlTable table;
    private final ImmutableList<InsertableSqlPrimaryKey> keys;
    private final ImmutableList<SqlSingleColumn> columns;
    private SubSelect subSelect;
    private ImmutableList<SqlColumn> returning;


    public InsertBuilder(Database database, SqlTable table, SqlPrimaryKey key,
                         ImmutableList<SqlSingleColumn> columns) {
        this(table, ImmutableList.of(new InsertableSqlPrimaryKey(key)), columns, database);
    }

    public InsertBuilder(Database database, SqlTable table, ImmutableList<SqlPrimaryKey> keys,
                         ImmutableList<SqlSingleColumn> columns) {
        super(database);
        this.table = table;
        this.keys = keys.stream().map(InsertableSqlPrimaryKey::new).collect(ImmutableList.toImmutableList());
        this.columns = columns;
    }

    public InsertBuilder(SqlTable table,
                         ImmutableList<InsertableSqlPrimaryKey> keys,
                         ImmutableList<SqlSingleColumn> columns,
                         Database database) {
        super(database);
        this.table = table;
        this.keys = keys;
        this.columns = columns;
    }

    public InsertBuilder values(SubSelect subSelect) {
        this.subSelect = subSelect;
        return this;
    }

    public InsertBuilder returning(ImmutableList<SqlColumn> columns) {
        returning = columns;
        return this;
    }

    public InsertBuilder returning(SqlColumn column) {
        returning = ImmutableList.of(column);
        return this;
    }

    public SqlQuery build() {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("INSERT INTO ");
        builder.append(table(this.table, false));
        builderColumn(builder, this.database().dialect());

        if (subSelect == null) {
            builderValues(builder);
        } else {
            builder.append("(").append(subSelect.sql()).append(")");
        }

        if (returning != null) {
            builder.append(" returning ");
            for (SqlColumn column : this.returning) {
                builder.append(column.name());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
        }

        String sql = builder.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }
        return new SqlQueryImpl(sql);
    }

    private void builderValues(StringBuilder builder) {
        builder.append(" VALUES (");
        for (int i = 0; i < this.keys.size(); i++) {
            DerivedColumn k = keys.get(i).getValue();
            if (k instanceof SqlPrimaryKey) {
                builder.append("?,");
            } else {
                builder.append(((AggregateFunction)k).build(database().dialect(), false));
                builder.append(",");
            }
        }
        for (SqlSingleColumn column : this.columns) {
            if (column.isInsertable()) {
                builder.append("?,");
            }
        }
        builder.setCharAt(builder.length() - 1, ')');
    }

    private void builderColumn(StringBuilder builder, Dialect dialect) {
        builder.append(" (");
        for (InsertableSqlPrimaryKey key : this.keys) {
            dialect.wrap(builder, key.getColumn(), false);
            builder.append(",");
        }
        for (SqlSingleColumn column : this.columns) {
            if (column.isInsertable()) {
                dialect.wrap(builder, column, false);
                builder.append(",");
            }
        }

        builder.setCharAt(builder.length() - 1, ')');
    }

}
