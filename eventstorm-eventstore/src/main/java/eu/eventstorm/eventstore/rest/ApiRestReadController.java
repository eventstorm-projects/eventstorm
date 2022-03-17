package eu.eventstorm.eventstore.rest;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import static eu.eventstorm.eventstore.StreamDefinitionException.newUnknownStream;

@RestController
@ConditionalOnProperty(prefix = "eu.eventstorm.eventstore.api", name = "read", havingValue = "true")
public final class ApiRestReadController {

    private final EventStore eventStore;
    private final StreamManager streamManager;

    public ApiRestReadController(EventStore eventStore, StreamManager streamManager) {
        this.eventStore = eventStore;
        this.streamManager = streamManager;
    }

    @GetMapping(path = "read/{stream}/{streamId}", produces = "application/x-protobuf")
    public Flux<Event> read(@PathVariable("stream") String stream, @PathVariable("streamId") String streamId) {

        StreamDefinition definition = streamManager.getDefinition(stream);

        if (definition == null) {
            throw newUnknownStream(stream);
        }

        return Mono.just(Tuples.of(definition, streamId))
                .flatMapMany(tuple -> Flux.fromStream(eventStore.readStream(tuple.getT1().getName(), tuple.getT2())));
    }

}
