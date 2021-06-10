package eu.eventstorm.core.validation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ConstraintViolations  {

    private ConstraintViolations() {
    }

    public static ConstraintViolation ofNullProperty(String property, String code) {
        return new NullConstraintViolation(property, code, of());
    }

    public static ConstraintViolation ofNullProperty(String property, String code, ImmutableMap<String,Object> params) {
        return new NullConstraintViolation(property, code, params);
    }

    public static ConstraintViolation ofAssertProperty(String property, String code, Object actual, Object expected) {
        return new PropertyAssertionConstraintViolation(property, code, actual, expected);
    }

    public static ConstraintViolation ofProperty(String property, String code, Object value) {
        return new PropertyConstraintViolation(property, code, value,  of());
    }

    public static ConstraintViolation ofProperty(String property, String code, Object value, ImmutableMap<String,Object> params) {
        return new PropertyConstraintViolation(property, code, value, params);
    }

    public static ConstraintViolation ofRule(String code) {
        return new ConstraintViolations.RuleConstraintViolation(code, of(), of());
    }

    public static ConstraintViolation ofRule(String code, ImmutableMap<String,Object> params) {
        return new ConstraintViolations.RuleConstraintViolation(code, params, of());
    }

    public static ConstraintViolation ofRule(String code, ImmutableMap<String,Object> params, ImmutableMap<String,String> urls) {
        return new ConstraintViolations.RuleConstraintViolation(code, params, urls);
    }

    private static final class RuleConstraintViolation extends DefaultConstraintViolation {
        private final ImmutableMap<String,String> urls;
        private RuleConstraintViolation(String code, ImmutableMap<String,Object> params, ImmutableMap<String,String> urls) {
            super(code, params);
            this.urls = urls;
        }
        public ImmutableMap<String, String> getUrls() {
            return urls;
        }
    }

    private static final class NullConstraintViolation extends DefaultPropertyConstraintViolation {
        private NullConstraintViolation(String property, String code, ImmutableMap<String,Object> params) {
            super(property,code,params);
        }
    }

    private static final class PropertyConstraintViolation extends DefaultPropertyConstraintViolation {
        private final Object value;
        private PropertyConstraintViolation(String property, String code, Object value, ImmutableMap<String,Object> params) {
            super(property, code, params);
            this.value = value;
        }
        public Object getValue() {
            return value;
        }
        @Override
        protected void doBuildMessage(StringBuilder builder) {
            super.doBuildMessage(builder);
            builder.append(" value=[").append(value).append("]");
        }
    }

    private static final class PropertyAssertionConstraintViolation extends DefaultPropertyConstraintViolation {
        private final Object actual;
        private final Object expected;
        private PropertyAssertionConstraintViolation(String property, String code, Object actual, Object expected) {
            super(property, code, of());
            this.actual = actual;
            this.expected = expected;
        }
        public Object getActual() {
            return actual;
        }
        public Object getExpected() {
            return expected;
        }
        @Override
        protected void doBuildMessage(StringBuilder builder) {
            super.doBuildMessage(builder);
            builder.append(" actual=[").append(actual).append("]");
            builder.append(" expected=[").append(expected).append("]");
        }
    }
}
