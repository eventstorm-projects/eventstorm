package eu.eventstorm.eventstore.rest;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamDefinitionException;
import eu.eventstorm.eventstore.StreamEventDefinition;
import eu.eventstorm.eventstore.StreamManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuples;

@RestController
public final class ApiRestController {

	private final EventStore eventStore;

	private final StreamManager streamManager;

	private final Scheduler scheduler;

	public ApiRestController(EventStore eventStore, StreamManager streamManager, Scheduler scheduler) {
		this.eventStore = eventStore;
		this.streamManager = streamManager;
		this.scheduler = scheduler;
	}

	@PostMapping(path = "append/{stream}/{streamId}/{eventPayloadType}", consumes = {"application/x-protobuf"})
	public Mono<Event> append(@PathVariable("stream") String stream, @PathVariable("streamId") String streamId,
	        @PathVariable("eventPayloadType") String payloadType, ServerHttpRequest request) {

		StreamDefinition definition = streamManager.getDefinition(stream);

		if (definition == null) {
			throw new StreamDefinitionException(StreamDefinitionException.Type.UNKNOW_STREAM, ImmutableMap.of("stream", stream));
		}
		
		StreamEventDefinition sepd = definition.getStreamEventDefinition(payloadType);
		
		if (sepd == null) {
			throw new StreamDefinitionException(StreamDefinitionException.Type.UNKNOW_STREAM, ImmutableMap.of("stream", stream, "payloadType", payloadType));
		}

		// @formatter:off
		return Mono.just(Tuples.of(sepd, streamId))
				.zipWith(DataBufferUtils.join(request.getBody()).map(buffer -> sepd.parse(buffer)))
				.publishOn(scheduler)
				.map(tuple -> eventStore.appendToStream(tuple.getT1().getT1().getStream(), tuple.getT1().getT2(), UUID.randomUUID().toString(), tuple.getT2()));
		// @formatter:on

	}

	@GetMapping(path = "read/{stream}/{streamId}")
	public Flux<Event> read(@PathVariable("stream") String stream, @PathVariable("streamId") String streamId) {

		StreamDefinition definition = streamManager.getDefinition(stream);

		if (definition == null) {
			throw new StreamDefinitionException(StreamDefinitionException.Type.UNKNOW_STREAM, ImmutableMap.of("stream", stream));
		}

		// @formatter:off
		return Mono.just(Tuples.of(definition, streamId))
				//.publishOn(scheduler)
		        .flatMapMany(tuple -> Flux.fromStream(eventStore.readStream(tuple.getT1().getName(), tuple.getT2())));
		// @formatter:on
	}

}
