package eu.eventstorm.core.validation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PropertyValidators {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyValidators.class);

	private PropertyValidators() {
	}

	private static final PropertyValidator<Object> NOT_NULL = (property, value, context) -> {
		if (value == null) {
			context.add(ConstraintViolations.ofNullProperty(property, "isNull"));
		}
	};
	
	private static final PropertyValidator<String> NOT_EMPTY = (property, value, context) -> {
		if (Strings.isEmpty(value)) {
			context.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
		}
	};
	
	
	private static final PropertyValidator<List<?>> LIST_NOT_EMPTY = (property, value, context) -> {
		if (value == null) {
			context.add(ConstraintViolations.ofNullProperty(property, "isNull"));
			return;
		}
		if (value.isEmpty()) {
			context.add(ConstraintViolations.ofNullProperty(property, "isEmpty"));
		}
	};
	
	public static PropertyValidator<String> size(int min, int max, String code) {
		return (property, value, context) -> {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("size({},{},{}) for [{}] with value [{}]", min, max, code, property, value);
			}
			int len = Strings.isEmpty(value) ? 0 : value.length();
			if (len < min) {
				context.add(ConstraintViolations.ofRule(code, ImmutableMap.of("property", property, "min", len)));
				return;
			}
			if (len > max) {
				context.add(ConstraintViolations.ofRule(code, ImmutableMap.of("property", property, "max", len)));
				return;
			}
		};
	}
	
	public static PropertyValidator<List<?>> listSize(int min, int max, String code) {
		return (property, value, context) -> {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("size({},{},{}) for [{}] with value [{}]", min, max, code, property, value);
			}
			int len = value.size();
			if (len <= min) {
				context.add(ConstraintViolations.ofRule(code, ImmutableMap.of("property", property, "min", len)));
				return;
			}
			if (len >= max) {
				context.add(ConstraintViolations.ofRule(code, ImmutableMap.of("property", property, "max", len)));
				return;
			}
		};
	}
	
	public static PropertyValidator<Object> notNull() {
		return NOT_NULL;
	}

	public static PropertyValidator<String> notEmpty() {
		return NOT_EMPTY;
	}
	
	public static PropertyValidator<List<?>> listNotEmpty() {
		return LIST_NOT_EMPTY;
	}
	
	public static <T> PropertyValidator<T> and(PropertyValidator<T> left, PropertyValidator<T> right) {
		return (property, value, context) -> {
			left.validate(property, value, context);
			if (!context.hasConstraintViolation()) {
				right.validate(property, value, context);
			}
		};
	}
	
	@SafeVarargs
	public static <T> PropertyValidator<T> and(PropertyValidator<T> left, PropertyValidator<T> right, PropertyValidator<T> ... others) {
		return (property, value, context) -> {
			and(left,right).validate(property, value, context);
			for (int i = 0 ; !context.hasConstraintViolation() && i < others.length; i++ ) {
				others[i].validate(property, value, context);
			}
		};
	}
	
}
