package eu.eventstorm.batch.rest;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class FileResourceReactiveControllerCondition extends AbstractRestEnabledCondition {

	@Override
	public boolean doMatches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return "MEMORY".equalsIgnoreCase(context.getEnvironment().getProperty("eu.eventstorm.batch.type"));
	}
	
}
