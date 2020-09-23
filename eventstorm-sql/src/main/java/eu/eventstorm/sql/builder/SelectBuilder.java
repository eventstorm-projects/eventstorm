package eu.eventstorm.sql.builder;

import static eu.eventstorm.sql.expression.Expressions.and;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Query;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.AggregateFunction;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.page.PageRequest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilder.class);

    public static final String METHOD = "method";
    
    private static final String LAMBDA_ARROW_OPERATOR = " -> ";
    private static final String LEFT_JOIN = "leftJoin()" + LAMBDA_ARROW_OPERATOR;
    private static final String RIGHT_JOIN = "rightJoin()" + LAMBDA_ARROW_OPERATOR;
    private static final String INNER_JOIN = "innerJoin()" + LAMBDA_ARROW_OPERATOR;

    private static final String NOT_NULL = " should be not null";
    private static final String TARGET_TABLE_NN = "targetTable" + NOT_NULL;
    private static final String TARGET_COLUMN_NN = "targetColumn" + NOT_NULL;
    private static final String FROM_NN = "from" + NOT_NULL;
    private static final String COLUMN_NN = "column" + NOT_NULL;
    private static final String EXPRESSION_NN = "expression " + LAMBDA_ARROW_OPERATOR;

    private final ImmutableList<SqlColumn> columns;
    private final AggregateFunction aggregateFunction;
    private Expression where;
    private ImmutableList<SqlTable> from;
    private final List<JoinClause> joins = new ArrayList<>();
    private final List<Order> orderBy = new ArrayList<>();
    private ImmutableList<SqlColumn> groupBy;
    private boolean forUpdate = false;
    private int limit = -1;
    private int offset = -1;
    private boolean pageable = false;

    public SelectBuilder(Database database, ImmutableList<SqlColumn> columns) {
    	this(database, columns, null);
    }
    
    public SelectBuilder(Database database, AggregateFunction function) {
        this(database, ImmutableList.of(), function);
    }
    
    public SelectBuilder(Database database, ImmutableList<SqlColumn> columns, AggregateFunction function) {
        super(database);
        this.columns = columns;
        this.aggregateFunction = function;
    }

    @SuppressWarnings("unchecked")
	public <T extends Query> T build() {

        validate();
        
        if (pageable) {
        	return (T) new SqlQueryPageableImpl(this , this.where == null ? 1 : countParamInWhereClause());	
        } 
        
        // not pageable => build sql
        StringBuilder builder = new StringBuilder(2048);
        appendSelect(builder);
        appendFrom(builder);
        appendJoins(builder);
        appendWhere(this.where, builder);
        appendGroupBy(builder);
        appendOrder(this.orderBy, builder);

        if (forUpdate) {
            builder.append(" FOR UPDATE");
        }

        if (limit != -1 && offset != -1) {
            builder.append(' ').append(this.database().dialect().range(offset, limit));
        } else if (limit != -1) {
        	builder.append(' ').append(this.database().dialect().limit(limit));
        } 
        
        String sql = builder.toString();
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }
        
        return (T) new SqlQueryImpl(sql);
        
    }

	private int countParamInWhereClause() {
		// TODO Auto-generated method stub
		return 0;
	}

	SqlQueryImpl buildPageableCount(PageRequest pageRequest) {
		SqlTable table = this.from.get(0);
		StringBuilder builder = new StringBuilder(1024);
		builder.append("SELECT ");
		boolean alias = hasAlias();

		builder.append("count(");
		if (alias) {
			builder.append(table.alias()).append(".");
		}
		builder.append("*) ");
		appendFrom(builder);
		appendWherePage(builder, pageRequest);

		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL Pageable Count [{}]", builder.toString());
		}

		return new SqlQueryImpl(builder.toString());
	}
	
	private void appendOrderPage(StringBuilder builder, PageRequest pageRequest) {
		if (!this.orderBy.isEmpty() && !pageRequest.getOrders().isEmpty()) {
			appendOrder(ImmutableList.<Order>builder().addAll(this.orderBy).addAll(pageRequest.getOrders()).build(), builder);
		} else if (!this.orderBy.isEmpty()) {
			appendOrder(this.orderBy, builder);
		} else if (!pageRequest.getOrders().isEmpty()) {
			appendOrder(pageRequest.getOrders(), builder);
		}
	}

	
	void appendWherePage(StringBuilder builder, PageRequest pageRequest) {
		if (where != null && pageRequest.getFilters().size() > 0) {
			appendWhere(Expressions.and(this.where, and(pageRequest.getFilters().toExpressions())), builder);
		} else if (where != null) {
			appendWhere(this.where, builder);
		} else if (pageRequest.getFilters().size() > 0) {
			appendWhere(and(pageRequest.getFilters().toExpressions()), builder);
		}	
	}

	SqlQueryImpl buildPageable(PageRequest pageRequest) {

//    	Module module = this.database().getModule(table);
//    	module.getDescriptor(table).ids();
		StringBuilder builder = new StringBuilder(1024);
		appendSelect(builder);
		appendFrom(builder);
		appendWherePage(builder, pageRequest);
		appendOrderPage(builder, pageRequest);

		builder.append(' ').append(this.database().dialect().range(pageRequest.getOffset(),
				pageRequest.getOffset() + pageRequest.getSize()));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL Pageable [{}]", builder.toString());
		}

		return new SqlQueryImpl(builder.toString());
	}
    
    
	public SelectBuilder forUpdate() {
        this.forUpdate = true;
        return this;
    }

    public SelectBuilder from(SqlTable... tables) {
        this.from = ImmutableList.copyOf(tables);
        return this;
    }

    public SelectBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SelectBuilder pageable() {
    	this.pageable = true;
    	return this;
    }
    
    public SelectBuilder orderBy(Order... orderings) {
        for (Order o : orderings) {
            this.orderBy.add(o);
        }
        return this;
    }

    public SelectBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public SelectBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SelectBuilder groupBy(SqlColumn... columns) {
        this.groupBy = ImmutableList.copyOf(columns);
        return this;
    }

    /**
     * Used when : select * from [_from_ JOIN_targetTable_ ON ]
     *
     * @param targetTable  is the first table to be joined
     * @param targetColumn is the _targetColumn_ used for the 'ON'
     * @param column       is the _from_ column used for the 'ON'
     * @return
     */
    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
    	return leftJoin(targetTable, targetColumn, column.table(), column);
    }

    /**
     * Used when : select * from _from_ on xxx JOIN _targetTable_ ON _otherColumn_=_targetColumn_
     *
     * @param targetTable  is the first table to be joined
     * @param targetColumn is the _targetColumn_ used for the 'ON'
     * @param otherFrom    is the first table to be joined
     * @param otherColumn  is the _from_ column used for the 'ON'
     * @return
     */
    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable otherFrom, SqlColumn otherColumn) {
       return leftJoin(targetTable, targetColumn, otherFrom, otherColumn, null);
    }

    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
    	return leftJoin(targetTable, targetColumn, column.table(), column, expression);
    }
    
    /**
     * Used when : select * from _from_ on xxx JOIN _targetTable_ ON _otherColumn_=_targetColumn_
     *
     * @param targetTable  is the first table to be joined
     * @param targetColumn is the _targetColumn_ used for the 'ON'
     * @param otherFrom    is the first table to be joined
     * @param otherColumn  is the _from_ column used for the 'ON'
     * @return
     */
    public SelectBuilder leftJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable otherFrom, SqlColumn otherColumn, Expression expression) {
        requireNonNull(targetTable, LEFT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, LEFT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(otherFrom, LEFT_JOIN + FROM_NN);
        requireNonNull(otherColumn, LEFT_JOIN + COLUMN_NN);

        if (this.from == null) {
            throw new SqlBuilderException(SqlBuilderException.Type.SELECT, ImmutableMap.of(METHOD,"leftJoin", "cause","call from() before this"));
        }

        if (!this.from.contains(otherFrom)) {
            throw new SqlBuilderException(SqlBuilderException.Type.SELECT, ImmutableMap.of(METHOD,"leftJoin", "cause ", "join column [" + otherColumn + "] not found in from clause"));
        }
        
        this.joins.add(new JoinClauseTable(this.database(), JoinType.LEFT, targetTable, targetColumn, otherFrom, otherColumn, expression));
        return this;
    }

    public SelectBuilder leftJoin(SubSelect subSelect, SqlColumn targetColumn, SqlTable otherFrom, SqlColumn otherColumn) {
        this.joins.add(new JoinClauseSubSelect(this.database(), JoinType.LEFT, subSelect, targetColumn, otherFrom, otherColumn));
    	return this;
    }
    

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        return innerJoin(targetTable, targetColumn, column.table(), column);
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn) {
        requireNonNull(targetTable, INNER_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, INNER_JOIN + TARGET_COLUMN_NN);
        requireNonNull(fromColumn, INNER_JOIN + COLUMN_NN);
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn));
        return this;
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column, Expression expression) {
        return innerJoin(targetTable, targetColumn, column.table(), column, expression);
    }

    public SelectBuilder innerJoin(SqlTable targetTable, SqlColumn targetColumn, SqlTable fromTable, SqlColumn fromColumn, Expression expression) {
        requireNonNull(targetTable, INNER_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, INNER_JOIN + TARGET_COLUMN_NN);
        requireNonNull(fromColumn, INNER_JOIN + COLUMN_NN);
        this.joins.add(new JoinClauseTable(this.database(), JoinType.INNER, targetTable, targetColumn, fromTable, fromColumn, expression));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column) {
        requireNonNull(targetTable, RIGHT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, RIGHT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, RIGHT_JOIN + COLUMN_NN);
        this.joins.add(new JoinClauseTable(this.database(), JoinType.RIGHT, targetTable, targetColumn, column.table(), column));
        return this;
    }

    public SelectBuilder rightJoin(SqlTable targetTable, SqlColumn targetColumn, SqlColumn column,
                                   Expression expression) {
        requireNonNull(targetTable, RIGHT_JOIN + TARGET_TABLE_NN);
        requireNonNull(targetColumn, RIGHT_JOIN + TARGET_COLUMN_NN);
        requireNonNull(column, RIGHT_JOIN + COLUMN_NN);
        requireNonNull(expression, RIGHT_JOIN + EXPRESSION_NN);
        this.joins.add(new JoinClauseTable(this.database(), JoinType.RIGHT, targetTable, targetColumn, column.table(), column, expression));
        return this;
    }

    private void appendSelect(StringBuilder builder) {
        builder.append("SELECT ");
        boolean alias = hasAlias();

        // Aggregate Functions
        if (aggregateFunction != null) {
            builder.append(aggregateFunction.build(database().dialect(), alias));
            builder.append(' ');
        } else {
            // OR Columns
            for (SqlColumn column : this.columns) {
                database().dialect().wrap(builder, column, alias);
                builder.append(',');
            }
            builder.setCharAt(builder.length() - 1, ' ');
        }
    }

    private void appendJoins(StringBuilder builder) {
        if (joins.isEmpty()) {
            return;
        }
        for (JoinClause clause : joins) {
            clause.build(builder);
        }
    }

    private void appendWhere(Expression where, StringBuilder builder) {
    	 if (where == null) {
             return;
         }
        builder.append(" WHERE ");
        builder.append(where.build(database().dialect(), hasAlias()));
    }

    private void appendOrder(List<Order> orderBy, StringBuilder builder) {
        if (orderBy.isEmpty()) {
            return;
        }
        builder.append(" ORDER BY ");
        for (Order o : orderBy) {
            database().dialect().wrap(builder, o.column(), hasAlias());
            builder.append(' ').append(o.type().name()).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
    }

    private void appendFrom(StringBuilder builder) {
        builder.append("FROM");
        if (this.from.size() == 1) {
            builder.append(' ');
            builder.append(table(from.get(0), (!joins.isEmpty())));
        } else {
            for (SqlTable table : this.from) {
                builder.append(' ');
                builder.append(table(table, true));
                builder.append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private void appendGroupBy(StringBuilder builder) {
        if (this.groupBy == null) {
            return;
        }
        builder.append(" GROUP BY ");

        for (SqlColumn column : this.groupBy) {
            database().dialect().wrap(builder, column, hasAlias());
            builder.append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
    }

    boolean hasAlias() {
    	if (this.from == null) {
    		throw new SqlBuilderException(SqlBuilderException.Type.SELECT, ImmutableMap.of("cause", "missing call from()"));
    	}
        return this.from.size() > 1 || !joins.isEmpty();
    }

    private void validate() {
        for (SqlColumn column : this.columns) {
            SqlTable table = column.table();
            table.alias();
        }
    }

    private static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new SqlBuilderException(SqlBuilderException.Type.SELECT, ImmutableMap.of(METHOD,"todo", "cause ", message));
        }
    }
}