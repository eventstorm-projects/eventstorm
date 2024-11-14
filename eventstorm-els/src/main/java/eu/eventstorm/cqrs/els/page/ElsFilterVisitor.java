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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsFilterVisitor implements FilterVisitor {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ElsFilterVisitor.class);

    private static final ImmutableMap<Operator, BiFunction<SinglePropertyFilter, ElsPageRequestDescriptor, Query>> EXPRESSIONS;

    static {
        EXPRESSIONS = ImmutableMap.<Operator, BiFunction<SinglePropertyFilter, ElsPageRequestDescriptor, Query>>builder()
                .put(Operator.IN, (filter,desc) -> {

                    ElsField field = desc.get(filter.getProperty());

                    TermsQueryField termsQueryField;
                    if (!filter.getValues().isEmpty()) {
                        termsQueryField = new TermsQueryField.Builder()
                                .value(filter.getValues().stream().map(FieldValue::of).toList())
                                .build();
                    } else {
                        String raw = filter.getRaw().substring(1, filter.getRaw().length() - 1);
                        termsQueryField = new TermsQueryField.Builder()
                                .value(Arrays.stream(raw.split(";"))
                                        .map(field::unwrap)
                                        .map(FieldValue::of).toList())
                                .build();
                    }

                    return new TermsQuery.Builder()
                            .field(field.termQueryField())
                            .terms(termsQueryField)
                            .build()
                            ._toQuery();

                })
                .put(Operator.CONTAINS, (filter,desc) -> {
                    return new MatchQuery.Builder()
                            .field(filter.getProperty())
                            .query(filter.getRaw().substring(1, filter.getRaw().length() - 1))
                            .build()
                            ._toQuery();
                })
                .build();
    }

    private final ElsPageRequestDescriptor elsPageRequestDescriptor;

    private Query query;

    private BoolQuery.Builder boolQuery;


    public ElsFilterVisitor(ElsPageRequestDescriptor elsPageRequestDescriptor) {
        this.elsPageRequestDescriptor = elsPageRequestDescriptor;
    }

    @Override
    public void visit(SinglePropertyFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitSinglePropertyFilter [{}]", filter);
        }
        Query query = EXPRESSIONS.get(filter.getOperator()).apply(filter, elsPageRequestDescriptor);
        if (boolQuery != null) {
            boolQuery.must(query);
        } else {
            this.query = query;;
        }
    }

    @Override
    public void visitBegin(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitBegin - AndFilter [{}]", filter);
        }
        boolQuery = new BoolQuery.Builder();
    }

    @Override
    public void visitEnd(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitEnd [{}]", filter);
        }
        query = boolQuery.build()._toQuery();

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

    public Query getQuery() {
        return query;
    }
}
