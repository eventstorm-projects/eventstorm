package eu.eventstorm.core.util;

import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface PropertyFactory<T,E extends Enum<E>> extends Function<T, E> {

}