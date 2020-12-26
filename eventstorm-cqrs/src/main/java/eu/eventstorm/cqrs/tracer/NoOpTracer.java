package eu.eventstorm.cqrs.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class NoOpTracer implements Tracer {

    public static final Tracer INSTANCE = new NoOpTracer();

    private static final Span NO_OP_SPAN = () -> {
    };

    private NoOpTracer() {
    }

    @Override
    public Span start(String name) {
        return NO_OP_SPAN;
    }
}
