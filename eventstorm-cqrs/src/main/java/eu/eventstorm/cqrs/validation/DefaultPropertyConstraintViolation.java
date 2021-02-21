package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class DefaultPropertyConstraintViolation extends  DefaultConstraintViolation {

    private final String property;

    DefaultPropertyConstraintViolation(String property, String code, ImmutableMap<String,Object> params) {
        super(code,params);
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

}