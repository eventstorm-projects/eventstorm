package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface JsonExpression {

    String getField();

    JsonOperation getOperation();

    Object getValue();
}
