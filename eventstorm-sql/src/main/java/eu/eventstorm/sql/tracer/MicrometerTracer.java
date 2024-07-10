package eu.eventstorm.sql.tracer;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.impl.TraceTransactionQueryContextImpl;
import eu.eventstorm.sql.impl.TransactionQueryContext;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Tracer;

import java.sql.PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class MicrometerTracer implements TransactionTracer {

    private final Tracer tracer;

    MicrometerTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    private record BraveTransactionSpan(ScopedSpan scopedSpan) implements TransactionSpan {

        @Override
        public void close() {
            this.scopedSpan.end();
        }

        @Override
        public void exception(Exception cause) {
            this.scopedSpan.error(cause);
        }

        @Override
        public void tag(String key, String value) {
            this.scopedSpan.tag(key, value);
        }

        @Override
        public void annotate(String annotation) {
            this.scopedSpan.event(annotation);
        }

    }

    @Override
    public TransactionSpan span(String name) {
        return new BraveTransactionSpan(tracer.startScopedSpan(name));
    }

    @Override
    public PreparedStatement decorate(PreparedStatement prepareStatement) {
        return new MicrometerPreparedStatement(prepareStatement, this);
    }

    @Override
    public TransactionQueryContext newTransactionContext(PreparedStatement ps, SqlQuery query) {
        return new TraceTransactionQueryContextImpl(ps, query, this);
    }

    @Override
    public TransactionSpan begin(Transaction transaction) {
        return new BraveTransactionSpan(tracer.startScopedSpan("transaction"));
    }

    Tracer getTracer() {
        return this.tracer;
    }
}
