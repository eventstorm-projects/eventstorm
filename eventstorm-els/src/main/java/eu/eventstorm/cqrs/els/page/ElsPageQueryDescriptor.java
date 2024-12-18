package eu.eventstorm.cqrs.els.page;

import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.page.EvaluatorDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsPageQueryDescriptor implements PageQueryDescriptor {

    private final ElsPageRequestDescriptor pageRequestDescriptor;

    public ElsPageQueryDescriptor(ElsPageRequestDescriptor pageRequestDescriptor) {
        this.pageRequestDescriptor = pageRequestDescriptor;
    }

    public ElsPageRequestDescriptor getPageRequestDescriptor() {
        return pageRequestDescriptor;
    }

    @Override
    public EvaluatorDefinition getEvaluator() {
        return new ElsEvaluatorDefinition(this.pageRequestDescriptor);
    }

}
