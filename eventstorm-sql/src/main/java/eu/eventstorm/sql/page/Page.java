package eu.eventstorm.sql.page;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Page<T> {

	Range getRange();

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    long getTotalElements();

    /**
     * Returns the page content as {@link Stream}.
     */
    Stream<T> getContent();

    public static <T> Page<T> empty() {
        return new PageImpl<>(Stream.empty(), 0, new Range(0,0));
    }

}
