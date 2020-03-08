package eu.eventstorm.eventstore.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import eu.eventstorm.eventstore.EventPayloadRegistry;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayloadImpl;

@SpringBootTest(classes = ApiRestControllerConfigurationTest.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiRestControllerTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@Autowired
	private EventPayloadRegistry registry;

	@Test
	void testAppend() {
		
		byte[] payload = registry.getSerializer(UserCreatedEventPayload.class.getName()).serialize(new UserCreatedEventPayloadImpl("ja","gmail",39));
		
		webTestClient.post()
			.uri("/append/{aggregateType}/{aggregateId}", UserCreatedEventPayload.class.getName(),"123")
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(payload))
			.exchange()
			.expectStatus().isOk()
			.expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));
	}
	

}
