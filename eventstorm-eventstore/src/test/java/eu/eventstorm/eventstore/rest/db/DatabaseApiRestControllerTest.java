package eu.eventstorm.eventstore.rest.db;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.util.List;

@ActiveProfiles("db")
@SpringBootTest(classes = DatabaseApiRestControllerTestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class DatabaseApiRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StreamManager am;

    @Test
    void testAppend() throws IOException {

        UserCreatedEventPayload payload = UserCreatedEventPayload.newBuilder()
                .setName("ja")
                .setEmail("jm@gmail.com")
                .setAge(40)
                .build();

        payload.writeDelimitedTo(System.out);

        //System.out.println();
        //System.out.println(JsonFormat.printer().print(payload));

        //UserCreatedEventPayload.parseFrom(data)
		
	/*	byte[] payload2 = am.getDefinition("user")
				.getStreamEvantPayloadDefinition(UserCreatedEventPayload.class.getSimpleName())
				.getPayloadSerializer()
				.serialize(new UserCreatedEventPayloadImpl("ja","gmail",39));
	*/
        webTestClient.post()
                .uri("/append/{stream}/{streamId}/{eventPayloadType}", "user", "123", UserCreatedEventPayload.class.getSimpleName())
                .contentType(MediaType.parseMediaType("application/x-protobuf"))
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));

        webTestClient.post()
                .uri("/append/{stream}/{streamId}/{eventPayloadType}", "user", "123", UserCreatedEventPayload.class.getSimpleName())
                .contentType(MediaType.parseMediaType("application/x-protobuf"))
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk()
                .expectBody().consumeWith(b -> System.out.println(new String(b.getResponseBodyContent())));


        FluxExchangeResult<Event> result = webTestClient.get()
                .uri("/read/{aggregateType}/{aggregateId}", "user", "123")
                .exchange()
                .returnResult(Event.class);


        List<Event> events = result.getResponseBody().collectList().block();
        Assertions.assertEquals("user", events.get(0).getStream());
        Assertions.assertEquals("user", events.get(1).getStream());

        result = webTestClient.get()
                .uri("/read/raw/{aggregateType}/{aggregateId}", "user", "123")
                .exchange()
                .returnResult(Event.class);

        events = result.getResponseBody().collectList().block();
        Assertions.assertEquals("user", events.get(0).getStream());
        Assertions.assertEquals("user", events.get(1).getStream());
        Assertions.assertEquals("{\"name\":\"ja\",\"age\":40,\"email\":\"jm@gmail.com\"}", events.get(0).getData().getValue().toStringUtf8());
        Assertions.assertEquals("UserCreatedEventPayload", events.get(0).getData().getTypeUrl());
        Assertions.assertEquals("{\"name\":\"ja\",\"age\":40,\"email\":\"jm@gmail.com\"}", events.get(1).getData().getValue().toStringUtf8());
        Assertions.assertEquals("UserCreatedEventPayload", events.get(1).getData().getTypeUrl());

    }


}
