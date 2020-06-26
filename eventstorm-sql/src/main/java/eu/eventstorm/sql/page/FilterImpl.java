package eu.eventstorm.sql.page;

import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class FilterImpl implements Filter {

	private final Expression expression;
	private final PreparedStatementIndexSetter psis;
	
	public FilterImpl(Expression expression) {
		this(expression, null);
	}

	public FilterImpl(Expression expression, PreparedStatementIndexSetter psis) {
		this.expression = expression;
		this.psis = psis;
	}

	@Override
	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public PreparedStatementIndexSetter getPreparedStatementIndexSetter() {
		return this.psis;
	}
	
}