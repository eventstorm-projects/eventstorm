package eu.eventstorm.cqrs.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Tracer {

    Span start(String name);
}