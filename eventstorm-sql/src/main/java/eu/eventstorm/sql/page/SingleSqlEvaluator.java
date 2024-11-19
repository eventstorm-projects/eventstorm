package eu.eventstorm.sql.page;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.page.AndFilter;
import eu.eventstorm.page.EvaluatorDefinition;
import eu.eventstorm.page.FilterVisitor;
import eu.eventstorm.page.MultiAndFilter;
import eu.eventstorm.page.Operator;
import eu.eventstorm.page.OrFilter;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.SinglePropertyFilter;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.Expressions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

public final class SingleSqlEvaluator implements EvaluatorDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleSqlEvaluator.class);

    private static final EnumMap<Operator, BiFunction<SqlColumn, SinglePropertyFilter, Expression>> EXPRESSIONS = new EnumMap<>(Operator.class);

    static {
        EXPRESSIONS.put(Operator.EQUALS, (col, filter) -> Expressions.eq(col));
        EXPRESSIONS.put(Operator.IN, (col, filter) -> Expressions.in(col, filter.getValues().size()));
        EXPRESSIONS.put(Operator.GREATER_EQUALS, (col, filter) -> Expressions.ge(col));
        EXPRESSIONS.put(Operator.GREATER, (col, filter) -> Expressions.gt(col));
        EXPRESSIONS.put(Operator.LESSER_EQUALS, (col, filter) -> Expressions.le(col));
        EXPRESSIONS.put(Operator.LESSER, (col, filter) -> Expressions.lt(col));
        EXPRESSIONS.put(Operator.CONTAINS, (col, filter) -> Expressions.like(col));
        EXPRESSIONS.put(Operator.STARTS_WITH, (col, filter) -> Expressions.like(col));
        EXPRESSIONS.put(Operator.ENDS_WITH, (col, filter) -> Expressions.like(col));
        EXPRESSIONS.put(Operator.NOT_IN, (col, filter) -> Expressions.not(Expressions.in(col, filter.getValues().size())));
    }

    private final SqlPageRequestDescriptor descriptor;

    public SingleSqlEvaluator(SqlPageRequestDescriptor sqlPageRequestDescriptor) {
        this.descriptor = sqlPageRequestDescriptor;
    }

    public Expression toExpressions(PageRequest pageRequest) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("toExpressions for [{}]", pageRequest);
        }

        Deque<FilterBuilder> stack = new ArrayDeque<>();
        FilterBuilder builder = new FilterBuilderAnd();
        stack.offer(builder);

        pageRequest.getFilter().accept(new FilterVisitor() {
            @Override
            public void visit(SinglePropertyFilter filter) {
                SqlColumn column = descriptor.get(filter.getProperty());
                BiFunction<SqlColumn, SinglePropertyFilter, Expression> builder = EXPRESSIONS.get(filter.getOperator());

                if (builder == null) {
                    throw new RuntimeException("Operator [" + filter.getOperator() + "] not supported");
                }
                stack.peekLast().add(builder.apply(column, filter));
            }

            @Override
            public void visitBegin(MultiAndFilter filter) {
                stack.add(new FilterBuilderAnd());
            }

            @Override
            public void visitBegin(AndFilter filter) {
                stack.add(new FilterBuilderAnd());
            }

            @Override
            public void visitBegin(OrFilter filter) {
                stack.add(new FilterBuilderOr());
            }

            @Override
            public void visitEnd(MultiAndFilter filter) {
                FilterBuilder builder = stack.pollLast();
                Expression multi = builder.build();
                stack.peekLast().add(multi);
            }

            @Override
            public void visitEnd(AndFilter filter) {
                FilterBuilder builder = stack.pollLast();
                Expression multi = builder.build();
                stack.peekLast().add(multi);
            }

            @Override
            public void visitEnd(OrFilter filter) {
                FilterBuilder builder = stack.pollLast();
                Expression multi = builder.build();
                stack.peekLast().add(multi);
            }
        });

        Expression expression = stack.getFirst().build();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("toExpressions result [{}]", expression);
        }

        return expression;
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
        if (Operator.STARTS_WITH == operator) {
            List<String> array = new ArrayList<>();
            for (String item : values) {
                array.add(item + '%');
            }
            return array;
        }
        if (Operator.ENDS_WITH == operator) {
            List<String> array = new ArrayList<>();
            for (String item : values) {
                array.add('%' + item);
            }
            return array;
        }

        return values;
    }


    private abstract static class FilterBuilder {

        private final ImmutableList.Builder<Expression> expressions = ImmutableList.builder();

        public Expression build() {

            ImmutableList<Expression> expressions = this.expressions.build();

            if (expressions.isEmpty()) {
                throw new IllegalStateException("Empty expression");
            }

            return doBuild(expressions);
        }

        protected abstract Expression doBuild(ImmutableList<Expression> expressions);

        public void add(Expression expression) {
            expressions.add(expression);
        }
    }

    private static class FilterBuilderAnd extends FilterBuilder {
        @Override
        protected Expression doBuild(ImmutableList<Expression> expressions) {
            if (expressions.size() == 1) {
                return expressions.get(0);
            }
            return Expressions.and(expressions);
        }
    }

    private static class FilterBuilderOr extends FilterBuilder {
        @Override
        protected Expression doBuild(ImmutableList<Expression> expressions) {
            if (expressions.size() == 1) {
                throw new IllegalStateException("[or] must have 2 or more expressions");
            }
            return Expressions.or(expressions);
        }
    }

}
