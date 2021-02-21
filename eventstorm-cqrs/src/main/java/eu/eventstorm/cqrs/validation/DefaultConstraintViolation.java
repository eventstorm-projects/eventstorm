package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.validation.ConstraintViolation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DefaultConstraintViolation implements ConstraintViolation {

    private final String code;
    private final ImmutableMap<String,Object> params;

    DefaultConstraintViolation(String code, ImmutableMap<String,Object> params) {
        this.code = code;
        this.params = params;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public ImmutableMap<String, Object> getParams() {
        return params;
    }

}