package eu.eventstorm.sql.page;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DefaultFilterEvaluator implements FilterEvaluator {

	private final Expression expression;
	private final List<String> values;
	private final PreparedStatementIndexSetter psis;
	
	public DefaultFilterEvaluator(Expression expression, List<String> values, PreparedStatementIndexSetter psis) {
		this.expression = expression;
		this.values = values;
		this.psis = psis;
	}

	@Override
	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public List<String> getValues() {
		return values;
	}

	@Override
	public int apply(PreparedStatement ps, int index) {
		if (psis == null) {
			return index;
		}
		try {
			return psis.set(ps, index);
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

}
