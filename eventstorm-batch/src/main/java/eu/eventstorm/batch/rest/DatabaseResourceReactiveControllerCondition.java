package eu.eventstorm.batch.rest;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DatabaseResourceReactiveControllerCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		 String value = context.getEnvironment().getProperty("eu.eventstorm.batch.resource.restEnabled");
		 if (!Strings.isEmpty(value) && !"true".equalsIgnoreCase(value)) {
			 return false;
		 }
		
		 value =  context.getEnvironment().getProperty("eu.eventstorm.batch.type");
		 if (!"DATABASE".equalsIgnoreCase(value)) {
			 return false;
		 }
		 return true;
	}
}
