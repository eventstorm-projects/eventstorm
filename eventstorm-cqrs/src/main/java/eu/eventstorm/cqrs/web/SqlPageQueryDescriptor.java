package eu.eventstorm.cqrs.web;

import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.page.EvaluatorDefinition;
import eu.eventstorm.sql.page.SingleSqlEvaluator;
import eu.eventstorm.sql.page.SqlPageRequestDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SqlPageQueryDescriptor implements PageQueryDescriptor {

    private final SqlPageRequestDescriptor pageRequestDescriptor;

    public SqlPageQueryDescriptor(SqlPageRequestDescriptor pageRequestDescriptor) {
        this.pageRequestDescriptor = pageRequestDescriptor;
    }

    @Override
    public EvaluatorDefinition getEvaluator() {
        return new SingleSqlEvaluator(this.pageRequestDescriptor);
    }

}
