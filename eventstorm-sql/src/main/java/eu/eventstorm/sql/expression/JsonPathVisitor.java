package eu.eventstorm.sql.expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface JsonPathVisitor {

    void visit(JsonPathLogicalExpression expression);

    void visit(JsonPathFieldStringExpression expression);

    void visit(JsonPathFieldsExpression expression);

    void visit(JsonPathArrayExpression expression);

    void visit(JsonPathRootExpression expression);

}
