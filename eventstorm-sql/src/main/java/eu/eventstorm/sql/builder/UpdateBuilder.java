package eu.eventstorm.sql.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.Expression;

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
    private Expression where;
    private final List<JoinClause> joins = new ArrayList<>();

    public UpdateBuilder(Database database, SqlTable table, ImmutableList<SqlSingleColumn> columns) {
        super(database);
        this.database = database;
        this.table = table;
        this.columns = columns;
    }

    public UpdateBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }
    
    public UpdateBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        return innerJoin(targetTable, targetColumn, column.table(), column);
    }

    public UpdateBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn) {
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn));
        return this;
    }

    public UpdateBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        return innerJoin(targetTable, targetColumn, column.table(), column, expression);
    }

    public UpdateBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn, Expression expression) {
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn, expression));
        return this;
    }

    public SqlQuery build() {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("UPDATE ");
        builder.append(table(this.table, this.joins.size() > 0));
        builderSetValues(builder, this.database.dialect());

    	appendJoins(builder);

    	if (where != null) {
    		builder.append(" WHERE ");
            builder.append(where.build(database.dialect(), this.joins.size() > 0));
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
                dialect.wrap(builder, column, this.joins.size() > 0);
                builder.append("=?,");
            }

        }
        builder.setLength(builder.length() - 1);
    }

    private void appendJoins(StringBuilder builder) {
        if (joins.isEmpty()) {
            return;
        }
        for (JoinClause clause : joins) {
            clause.build(builder);
        }
    }
}