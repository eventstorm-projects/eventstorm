package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ConstraintViolation {

    ImmutableList<String> getProperties();

    String getCause();
    
}