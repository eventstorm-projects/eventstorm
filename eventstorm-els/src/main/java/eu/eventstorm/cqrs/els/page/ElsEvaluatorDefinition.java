package eu.eventstorm.cqrs.els.page;

import eu.eventstorm.page.EvaluatorDefinition;
import eu.eventstorm.page.Operator;
import org.slf4j.Logger;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsEvaluatorDefinition implements EvaluatorDefinition {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ElsEvaluatorDefinition.class);

    private final ElsPageRequestDescriptor pageRequestDescriptor;

    ElsEvaluatorDefinition(ElsPageRequestDescriptor pageRequestDescriptor) {
        this.pageRequestDescriptor = pageRequestDescriptor;
    }

    @Override
    public List<String> enrich(Operator operator, List<String> values) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("enrich [" + operator + "] with [" + values + "]");
        }

        return List.of();
    }

}
