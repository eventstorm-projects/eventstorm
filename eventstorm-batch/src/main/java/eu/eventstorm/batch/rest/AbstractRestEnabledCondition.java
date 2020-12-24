package eu.eventstorm.batch.rest;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractRestEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String value = context.getEnvironment().getProperty("eu.eventstorm.batch.resource.rest-enabled");
        if (Strings.isEmpty(value)) {
            return false;
        }
        if (!"true".equalsIgnoreCase(value)) {
            return false;
        }

        return doMatches(context, metadata);
    }

    protected abstract boolean doMatches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
