package eu.eventstorm.core.validation;

import java.util.function.Consumer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface ValidatorContext {

    boolean hasConstraintViolation();

    String getCode();

    void forEach(Consumer<ConstraintViolation> consumer);

    void add(ConstraintViolation constraintViolation);

    <T> void put(String key, T value);

    <T> T get(String key);

}