package eu.eventstorm.batch.rest;

import eu.eventstorm.util.Strings;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DatabaseResourceReactiveControllerCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return !Strings.isEmpty(context.getEnvironment().getProperty("eu.eventstorm.batch.execution.context-path"));
	}

}