package eu.eventstorm.cqrs.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Span extends AutoCloseable {

    @Override
    void close();

}
