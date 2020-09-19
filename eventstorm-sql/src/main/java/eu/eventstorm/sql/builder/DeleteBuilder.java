package eu.eventstorm.sql.builder;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DeleteBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteBuilder.class);

    private final Database database;
    private final SqlTable table;
    private Expression where;
    private final List<JoinClause> joins = new ArrayList<>();
    
    public DeleteBuilder(Database database, SqlTable table) {
        super(database);
        this.database = database;
        this.table = table;
    }

    public DeleteBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public DeleteBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        return innerJoin(targetTable, targetColumn, column.table(), column);
    }

    public DeleteBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn) {
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn));
        return this;
    }

    public DeleteBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        return innerJoin(targetTable, targetColumn, column.table(), column, expression);
    }

    public DeleteBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn, Expression expression) {
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn, expression));
        return this;
    }
    
    public SqlQuery build() {
    	StringBuilder builder = new StringBuilder(2048);
    	builder.append("DELETE FROM ");
    	builder.append(table(this.table,  this.joins.size() > 0));
    	appendJoins(builder);
    	builder.append(" WHERE ");
    	builder.append(where.build(database.dialect(), this.joins.size() > 0));
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", builder);
        }

        return new SqlQueryImpl(builder.toString());
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