package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ConstraintViolation {

    String getCode();

    ImmutableMap<String, Object> getParams();

    void buildMessage(StringBuilder builder);
}