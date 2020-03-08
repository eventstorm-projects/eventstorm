package eu.eventstorm.eventstore.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.id.AggregateIds;
import eu.eventstorm.eventstore.EventPayloadRegistry;
import eu.eventstorm.eventstore.EventStore;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

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

		LOGGER.info("append tp [{}] with id [{}]", aggregateType, aggregateId);
		
		;
		return Mono.just(aggregateType)
			.map(type -> registry.getDeserializer(type))
			.zipWith(DataBufferUtils.join(request.getBody()).map( buffer -> {
				 byte[] result = new byte[buffer.readableByteCount()];
			     buffer.read(result);
			     DataBufferUtils.release(buffer);
			     return result;
			}))
			.map(tuple -> tuple.getT1().deserialize(tuple.getT2()))
			.publishOn(scheduler)
			.map(payload -> eventStore.appendToStream(aggregateType, AggregateIds.from(aggregateId), payload));

	}

}
