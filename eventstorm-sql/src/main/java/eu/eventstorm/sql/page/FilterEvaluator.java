package eu.eventstorm.sql.page;

import java.sql.PreparedStatement;
import java.util.List;

import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface FilterEvaluator {

	Expression getExpression();

	List<String> getValues();

	int apply(PreparedStatement ps, int i);
}
