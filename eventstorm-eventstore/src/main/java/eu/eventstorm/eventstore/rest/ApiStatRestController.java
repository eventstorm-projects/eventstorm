package eu.eventstorm.eventstore.rest;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.eventstore.EventStore;
import reactor.core.publisher.Mono;

@RestController
@ConditionalOnProperty(prefix = "eu.eventstorm.eventstore.api", name = "stats", havingValue = "true")
public final class ApiStatRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiStatRestController.class);
	
	private final EventStore eventStore;

	public ApiStatRestController(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@PostMapping(path = "stat/{stream}/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String> statStream(@PathVariable("stream") String stream) {

		String validStream = Encode.forUriComponent(stream);
		
		LOGGER.info("statStream({})", Encode.forUriComponent(validStream));

		this.eventStore.stat(Encode.forUriComponent(validStream));
		
		return Mono.just("toto");
		
	}

		
}
