package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.validation.ConstraintViolation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class DefaultConstraintViolation implements ConstraintViolation {

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

    @Override
    public final void buildMessage(StringBuilder builder) {
        builder.append("[").append(getClass().getSimpleName()).append("] : code=[").append(code).append("]");
        doBuildMessage(builder);
        if (params.size()>0) {
            params.forEach((k,v) -> builder.append(" [").append(k).append("]=[").append(v).append("]"));
        }
    }

    protected void doBuildMessage(StringBuilder builder) {

    }

}