package eu.eventstorm.eventstore.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.eventstore.EventStore;
import reactor.core.publisher.Mono;

@RestController
public final class ApiStatRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiStatRestController.class);
	
	private final EventStore eventStore;

	public ApiStatRestController(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@PostMapping(path = "stat/{stream}/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> statStream(@PathVariable("stream") String stream) {
		
		LOGGER.info("statStream({})", stream);

		this.eventStore.stat(stream);
		
		
		return Mono.just("toto");
		
	}

		
}
