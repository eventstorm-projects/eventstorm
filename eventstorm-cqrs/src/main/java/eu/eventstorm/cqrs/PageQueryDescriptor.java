package eu.eventstorm.cqrs;

import eu.eventstorm.page.EvaluatorDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface PageQueryDescriptor {

    EvaluatorDefinition getEvaluator();

}
