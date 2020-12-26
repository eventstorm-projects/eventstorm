package eu.eventstorm.cqrs.tracer;

import brave.Tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class BraveSpan implements Span {

    private final brave.Span span;
    private final Tracer.SpanInScope scope;

    public BraveSpan(Tracer.SpanInScope scope, brave.Span newSpan) {
        this.scope = scope;
        this.span = newSpan;
    }

    @Override
    public void close() {
        try {
            scope.close();
        } finally {
            this.span.finish();
        }
    }
}
