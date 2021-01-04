package eu.eventstorm.cqrs.tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BraveTracer implements Tracer {

    private final brave.Tracer tracer;

    public BraveTracer(brave.Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Span start(String name) {
        brave.Span newSpan = this.tracer.nextSpan();
        newSpan.name(name);
        newSpan.tag("thread", Thread.currentThread().getName());
        newSpan.start();
        return new BraveSpan(this.tracer.withSpanInScope(newSpan), newSpan);
    }
}
