package eu.eventstorm.eventstore.rest;

import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamEventDefinition;
import eu.eventstorm.eventstore.StreamManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import static eu.eventstorm.eventstore.StreamDefinitionException.newUnknownStream;
import static eu.eventstorm.eventstore.StreamDefinitionException.newUnknownStreamType;

@RestController
@ConditionalOnProperty(prefix = "eu.eventstorm.eventstore.api", name = "append", havingValue ="true")
public final class ApiRestWriteController {

    private final EventStore eventStore;

    private final StreamManager streamManager;

    public ApiRestWriteController(EventStore eventStore, StreamManager streamManager) {
        this.eventStore = eventStore;
        this.streamManager = streamManager;
    }

    @ConditionalOnProperty(prefix = "eu.eventstorm.eventstore.api", name = "write", havingValue = "false")
    @PostMapping(path = "append/{stream}/{streamId}/{eventType}", consumes = {"application/x-protobuf"})
    public Mono<Event> append(@PathVariable("stream") String stream, @PathVariable("streamId") String streamId,
                              @PathVariable("eventType") String eventType, ServerHttpRequest request) {

        StreamDefinition definition = streamManager.getDefinition(stream);

        if (definition == null) {
            throw newUnknownStream(stream);
        }

        StreamEventDefinition sepd = definition.getStreamEventDefinition(eventType);

        if (sepd == null) {
            throw newUnknownStreamType(stream, eventType);
        }

        return Mono.just(Tuples.of(sepd, streamId))
                .zipWith(DataBufferUtils.join(request.getBody()).map(sepd::parse))
                .map(tuple -> {
                    EventCandidate<Message> candidate = new EventCandidate<>(tuple.getT1().getT1().getStream(), tuple.getT1().getT2(), tuple.getT2());
                    return eventStore.appendToStream(candidate, null);
                });

    }

}
