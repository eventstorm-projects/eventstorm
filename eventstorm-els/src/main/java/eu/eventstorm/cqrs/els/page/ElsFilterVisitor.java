package eu.eventstorm.cqrs.els.page;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.page.AndFilter;
import eu.eventstorm.page.FilterVisitor;
import eu.eventstorm.page.MultiAndFilter;
import eu.eventstorm.page.Operator;
import eu.eventstorm.page.OrFilter;
import eu.eventstorm.page.SinglePropertyFilter;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsFilterVisitor implements FilterVisitor {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ElsFilterVisitor.class);

    private static final ImmutableMap<Operator, BiConsumer<Query.Builder, SinglePropertyFilter>> EXPRESSIONS;

    static {
        EXPRESSIONS = ImmutableMap.<Operator, BiConsumer<Query.Builder, SinglePropertyFilter>>builder()
                .put(Operator.IN, (builder, filter) -> {

                    TermsQueryField termsQueryField;
                    if (!filter.getValues().isEmpty()) {
                        termsQueryField = new TermsQueryField.Builder()
                                .value(filter.getValues().stream().map(FieldValue::of).toList())
                                .build();
                    } else {
                        String raw = filter.getRaw().substring(1, filter.getRaw().length() - 1);
                        termsQueryField = new TermsQueryField.Builder()
                                .value(Arrays.stream(raw.split(";"))
                                        .map(s -> s.substring(1, s.length() - 1))
                                        .map(FieldValue::of).toList())
                                .build();
                    }

                    TermsQuery termsQuery = new TermsQuery.Builder()
                            .field(filter.getProperty()+".keyword")
                            .terms(termsQueryField)
                            .build();

                    builder.terms(termsQuery);
                })
                .put(Operator.CONTAINS, (builder, filter) -> {
                    MatchQuery matchQuery = new MatchQuery.Builder()
                            .field(filter.getProperty())
                            .query(filter.getRaw().substring(1, filter.getRaw().length() - 1))
                            .build();
                    builder.match(matchQuery);
                })
                .build();
    }


    private final Query.Builder builder;


    public ElsFilterVisitor(Query.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void visit(SinglePropertyFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitSinglePropertyFilter [{}]", filter);
        }

        EXPRESSIONS.get(filter.getOperator()).accept(builder, filter);
    }

    @Override
    public void visitBegin(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitBegin - AndFilter [{}]", filter);
        }
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

    }

    @Override
    public void visitEnd(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitEnd [{}]", filter);
        }

        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitBegin(OrFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitBegin [{}]", filter);
        }
    }

    @Override
    public void visitEnd(OrFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitEnd [{}]", filter);
        }
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitBegin(MultiAndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitBegin [{}]", filter);
        }

    }

    @Override
    public void visitEnd(MultiAndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitEnd [{}]", filter);
        }

    }
}
