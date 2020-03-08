package eu.eventstorm.eventstore.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.id.AggregateIds;
import eu.eventstorm.eventstore.EventPayloadRegistry;
import eu.eventstorm.eventstore.EventStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuples;

@RestController
public final class ApiRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiRestController.class);
	
	private final EventStore eventStore;
	
	private final EventPayloadRegistry registry;
	
	private final Scheduler scheduler;
	
	public ApiRestController(EventStore eventStore, EventPayloadRegistry registry, Scheduler scheduler) {
		this.eventStore = eventStore;
		this.registry = registry;
		this.scheduler = scheduler;
	}

	@PostMapping(path = "append/{aggregateType}/{aggregateId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Event<?>> append(@PathVariable("aggregateType") String aggregateType, @PathVariable("aggregateId") String aggregateId, ServerHttpRequest request) {
		return Mono.just(aggregateType)
			.map(type -> registry.getDeserializer(type))
			.zipWith(DataBufferUtils.join(request.getBody()).map( buffer -> {
				 byte[] result = new byte[buffer.readableByteCount()];
			     buffer.read(result);
			     DataBufferUtils.release(buffer);
			     return result;
			}))
			.map(tuple -> Tuples.of(tuple.getT1().deserialize(tuple.getT2()), tuple.getT2()))
			.publishOn(scheduler)
			.map(tuple -> eventStore.appendToStream(aggregateType, AggregateIds.from(aggregateId), tuple.getT1(), tuple.getT2()));
	}
	
	@GetMapping(path = "read/{aggregateType}/{aggregateId}")
	public Flux<Event<?>> read(@PathVariable("aggregateType") String aggregateType, @PathVariable("aggregateId") String aggregateId) {
		return Mono.just(Tuples.of(aggregateType, aggregateId))
				.publishOn(scheduler)
				.flatMapMany(tuple -> Flux.fromStream(eventStore.readStream(tuple.getT1(), AggregateIds.from(tuple.getT2()))));
	}

}
