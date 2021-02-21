package eu.eventstorm.cqrs.validation;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.validation.ConstraintViolation;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ConstraintViolations  {

    private ConstraintViolations() {
    }

    public static ConstraintViolation ofNullProperty(String property, String code) {
        return new NullConstraintViolation(property, code, ImmutableMap.of());
    }

    public static ConstraintViolation ofNullProperty(String property, String code, ImmutableMap<String,Object> params) {
        return new NullConstraintViolation(property, code, params);
    }

    public static ConstraintViolation ofProperty(String property, String code, Object actual, Object expected) {
        return new PropertyAssertionConstraintViolation(property, code, actual, expected);
    }

    public static ConstraintViolation ofProperty(String property, String code, Object value) {
        return new PropertyConstraintViolation(property, code, value);
    }

    public static ConstraintViolation ofRule(String code) {
        return new ConstraintViolations.RuleConstraintViolation(code, ImmutableMap.of());
    }

    public static ConstraintViolation ofRule(String code, ImmutableMap<String,Object> params) {
        return new ConstraintViolations.RuleConstraintViolation(code, params);
    }

    private static final class RuleConstraintViolation extends DefaultConstraintViolation {
        private RuleConstraintViolation(String code, ImmutableMap<String,Object> params) {
            super(code, params);
        }
    }

    private static final class NullConstraintViolation extends DefaultPropertyConstraintViolation {
        private NullConstraintViolation(String property, String code, ImmutableMap<String,Object> params) {
            super(property,code,params);
        }
    }

    private static final class PropertyConstraintViolation extends DefaultPropertyConstraintViolation {
        private final Object value;
        private PropertyConstraintViolation(String property, String code, Object value) {
            super(property, code, ImmutableMap.of());
            this.value = value;
        }
        public Object getValue() {
            return value;
        }
    }

    private static final class PropertyAssertionConstraintViolation extends DefaultPropertyConstraintViolation {
        private final Object actual;
        private final Object expected;
        private PropertyAssertionConstraintViolation(String property, String code, Object actual, Object expected) {
            super(property, code, ImmutableMap.of());
            this.actual = actual;
            this.expected = expected;
        }
        public Object getActual() {
            return actual;
        }
        public Object getExpected() {
            return expected;
        }
    }
}
