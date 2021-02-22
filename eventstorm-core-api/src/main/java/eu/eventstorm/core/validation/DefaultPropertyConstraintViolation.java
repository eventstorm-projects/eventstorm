package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class DefaultPropertyConstraintViolation extends DefaultConstraintViolation {

    private final String property;

    DefaultPropertyConstraintViolation(String property, String code, ImmutableMap<String,Object> params) {
        super(code,params);
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    @Override
    protected void doBuildMessage(StringBuilder builder) {
        builder.append(" property=[").append(property).append("]");
    }

}