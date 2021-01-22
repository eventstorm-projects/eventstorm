package eu.eventstorm.sql.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LogicalExpression implements Expression{

    /**
     * SLF4J Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogicalExpression.class);

    private final String operation;
    
    private final ImmutableList<Expression> expressions;

    LogicalExpression(String operation, ImmutableList<Expression> expressions) {
    	this.operation = operation;
    	this.expressions = expressions;
    }

    @Override
    public String build(Dialect dialect, boolean alias) {
    	if (this.expressions.size() == 1) {
    		return this.expressions.get(0).build(dialect, alias);
    	}
        StringBuilder builder = new StringBuilder(512);
        builder.append('(');
        builder.append(this.expressions.get(0).build(dialect, alias));
        for (int i = 1, n = this.expressions.size(); i < n ; i++) {
        	builder.append(' ');
        	builder.append(this.operation);
        	builder.append(' ');
        	builder.append(this.expressions.get(i).build(dialect, alias));
        }
        builder.append(')');

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Fragment LogicalExpression -> [{}]", builder);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(512);
        builder.append('(');
        builder.append(this.expressions.get(0).toString());
        for (int i = 1, n = this.expressions.size(); i < n ; i++) {
        	builder.append(' ');
        	builder.append(this.operation);
        	builder.append(' ');
        	builder.append(this.expressions.get(i).toString());
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public int countParameter() {
        return expressions.stream().mapToInt(Expression::countParameter).sum();
    }
}