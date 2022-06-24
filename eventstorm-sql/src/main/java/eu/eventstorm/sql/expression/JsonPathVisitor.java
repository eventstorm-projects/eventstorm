package eu.eventstorm.sql.expression;

public interface JsonPathVisitor {

    void visit(JsonPathLogicalExpression expression);

    void visit(JsonPathFieldStringExpression expression);

    void visit(JsonPathFieldsExpression expression);

    void visit(JsonPathArrayExpression expression);

    void visit(JsonPathRootExpression expression);

}
