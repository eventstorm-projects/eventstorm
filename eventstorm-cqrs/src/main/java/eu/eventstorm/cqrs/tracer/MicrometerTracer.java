package eu.eventstorm.cqrs.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class MicrometerTracer implements Tracer {

    private final io.micrometer.tracing.Tracer tracer;

    public MicrometerTracer(io.micrometer.tracing.Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Span start(String name) {
        io.micrometer.tracing.Span newSpan = this.tracer.nextSpan();
        newSpan.name(name);
        newSpan.tag("thread", Thread.currentThread().getName());
        newSpan.start();
        return new MicrometerSpan(this.tracer.withSpan(newSpan), newSpan);
    }

}
