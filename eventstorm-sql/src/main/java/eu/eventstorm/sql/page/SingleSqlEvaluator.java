package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.page.EvaluatorDefinition;
import eu.eventstorm.page.Filter;
import eu.eventstorm.page.Operator;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.Expressions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

public final class SingleSqlEvaluator implements EvaluatorDefinition {

    private static final EnumMap<Operator, BiFunction<SqlColumn, Filter,Expression >> EXPRESSIONS = new EnumMap<>(Operator.class);

    static {
        EXPRESSIONS.put(Operator.EQUALS, (col, filter) -> Expressions.eq(col));
        EXPRESSIONS.put(Operator.IN, (col, filter) -> Expressions.in(col, filter.getValues().size()));
        EXPRESSIONS.put(Operator.GREATER_EQUALS, (col, filter) -> Expressions.ge(col));
        EXPRESSIONS.put(Operator.CONTAINS, (col, filter) -> Expressions.like(col));
    }

    private final SqlPageRequestDescriptor descriptor;

    public SingleSqlEvaluator(SqlPageRequestDescriptor sqlPageRequestDescriptor) {
        this.descriptor = sqlPageRequestDescriptor;
    }

    public ImmutableList<Expression> toExpressions(PageRequest pageRequest) {

        ImmutableList.Builder<Expression> expressionBuilder = ImmutableList.builder();

        for (Filter filter : pageRequest.getFilters()) {

            SqlColumn column = descriptor.get(filter.getProperty());
            BiFunction<SqlColumn, Filter, Expression> builder = EXPRESSIONS.get(filter.getOperator());

            if (builder == null) {
                throw new RuntimeException("Operator [" + filter.getOperator() + "] not supported");
            }

            expressionBuilder.add(builder.apply(column, filter));
        }

        return expressionBuilder.build();
    }

    public SqlPageRequestDescriptor getSqlPageRequestDescriptor() {
        return this.descriptor;
    }

    @Override
    public List<String> enrich(Operator operator, List<String> values) {
        if (Operator.CONTAINS == operator) {
            List<String> array = new ArrayList<>();
            for (String item : values) {
                array.add('%' + item + '%');
            }
            return array;
        }
        return values;
    }
}
