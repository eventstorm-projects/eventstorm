package eu.eventstorm.cqrs.els.page;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import eu.eventstorm.page.AndFilter;
import eu.eventstorm.page.FilterVisitor;
import eu.eventstorm.page.MultiAndFilter;
import eu.eventstorm.page.OrFilter;
import eu.eventstorm.page.SinglePropertyFilter;
import org.slf4j.Logger;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsFilterVisitor implements FilterVisitor {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ElsFilterVisitor.class);

    private final Query.Builder builder;

    public ElsFilterVisitor(Query.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void visit(SinglePropertyFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitSinglePropertyFilter [{}]", filter);
        }
    }

    @Override
    public void visitBegin(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitBegin [{}]", filter);
        }
    }

    @Override
    public void visitEnd(AndFilter filter) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("visitEnd [{}]", filter);
        }
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
