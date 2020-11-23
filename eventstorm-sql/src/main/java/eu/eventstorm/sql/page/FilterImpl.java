package eu.eventstorm.sql.page;

import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class FilterImpl implements Filter {

	private final String property;
	private final String operator;
	private final String value;
	private final Expression expression;
	private final PreparedStatementIndexSetter psis;
	
	public FilterImpl(String property, String operator, String value, Expression expression, PreparedStatementIndexSetter psis) {
		this.property = property;
		this.operator = operator;
		this.value = value;
		this.expression = expression;
		this.psis = psis;
	}


	@Override
	public String getProperty() {
		return this.property;
	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public PreparedStatementIndexSetter getPreparedStatementIndexSetter() {
		return this.psis;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("expression", expression)
				.toString();
	}
	
}