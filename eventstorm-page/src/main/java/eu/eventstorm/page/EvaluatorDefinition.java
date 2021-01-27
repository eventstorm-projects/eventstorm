package eu.eventstorm.page;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EvaluatorDefinition {

    List<String> enrich(Operator operator, List<String> values);

}
