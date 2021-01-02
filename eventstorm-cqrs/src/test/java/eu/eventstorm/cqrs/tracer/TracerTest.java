package eu.eventstorm.cqrs.tracer;

import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import zipkin2.reporter.brave.ZipkinSpanHandler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TracerTest {

    @Test
    void noOpTracerTest() {

        Tracer tracer = NoOpTracer.INSTANCE;

        try (Span ignored = tracer.start("Test")) {
            assertNull(Tracing.currentTracer());
        }

    }

    @Test
    void braveTracerTest() {

        Tracer tracer = new BraveTracer(Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).addSpanHandler(ZipkinSpanHandler.create(new LoggingBraveReporter())).build().tracer());

        try (Span ignored = tracer.start("Test")) {
            assertNotNull(Tracing.currentTracer());
            assertNotNull(Tracing.currentTracer().currentSpan());
        }

    }
}

