package eu.eventstorm.cqrs.tracer;

import io.micrometer.tracing.Tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MicrometerSpan implements Span {

    private final io.micrometer.tracing.Span span;
    private final Tracer.SpanInScope scope;

    public MicrometerSpan(Tracer.SpanInScope scope, io.micrometer.tracing.Span newSpan) {
        this.scope = scope;
        this.span = newSpan;
    }

    @Override
    public void close() {
        try {
            scope.close();
        } finally {
            this.span.end();
        }
    }
}
