package eu.eventstorm.eventstore.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayloadImpl;

@SpringBootTest(classes = ApiRestControllerConfigurationTest.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiRestControllerTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@Autowired
	private StreamManager am;
	
	@Test
	void testAppend() {
		
		byte[] payload = am.getDefinition("user")
				.getStreamEvantPayloadDefinition(UserCreatedEventPayload.class.getSimpleName())
				.getPayloadSerializer()
				.serialize(new UserCreatedEventPayloadImpl("ja","gmail",39));
		
		webTestClient.post()
			.uri("/append/{stream}/{streamId}/{eventPayloadType}", "user","123", UserCreatedEventPayload.class.getSimpleName())
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(payload))
			.exchange()
			.expectStatus().isOk()
			.expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));
		
		webTestClient.post()
		.uri("/append/{stream}/{streamId}/{eventPayloadType}", "user","123", UserCreatedEventPayload.class.getSimpleName())
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(payload))
			.exchange()
			.expectStatus().isOk()
			.expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));
		
		webTestClient.get()
			.uri("/read/{aggregateType}/{aggregateId}", "user","123")
			.exchange()
			.expectStatus().isOk()
			.expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));
	}
	

}
