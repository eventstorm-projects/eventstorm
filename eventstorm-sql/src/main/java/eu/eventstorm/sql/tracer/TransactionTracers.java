package eu.eventstorm.sql.tracer;


import io.micrometer.tracing.Tracer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionTracers {

    private TransactionTracers() {
    }

    public static TransactionTracer noOp() {
        return new NoOpTracer();
    }

    public static TransactionTracer debug() {
        return new DebugTracer();
    }

    @Deprecated(forRemoval = true)
    public static TransactionTracer brave(brave.Tracer tracer) {
        return new BraveTracer(tracer);
    }

    public static TransactionTracer micrometer(Tracer tracer) {
        return new MicrometerTracer(tracer);
    }

}