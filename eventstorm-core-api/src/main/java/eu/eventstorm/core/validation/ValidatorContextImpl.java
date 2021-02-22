package eu.eventstorm.core.validation;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class ValidatorContextImpl implements ValidatorContext {

    private final Map<String, Object> props = new HashMap<>();
    private List<ConstraintViolation> violations = ImmutableList.of();

    @Override
    public <T> void put(String key, T value) {
        this.props.put(key, value);
    }

    @Override
    public <T> T get(String key) {
        return (T)this.props.get(key);
    }

    public boolean hasConstraintViolation() {
        return !violations.isEmpty();
    }

    public void add(ImmutableList<ConstraintViolation> list) {
        if (list.isEmpty()) {
            return;
        }
        if (violations.isEmpty()) {
            violations = new ArrayList<>();
        }
        this.violations.addAll(list);
    }

    public void add(ConstraintViolation constraintViolation) {
        if (violations.isEmpty()) {
            violations = new ArrayList<>();
        }
        violations.add(constraintViolation);
    }

    public String getCode() {
        return violations.stream().map(ConstraintViolation::getCode).collect(joining(","));
    }

    public void forEach(Consumer<ConstraintViolation> consumer) {
        this.violations.forEach(consumer);
    }

}