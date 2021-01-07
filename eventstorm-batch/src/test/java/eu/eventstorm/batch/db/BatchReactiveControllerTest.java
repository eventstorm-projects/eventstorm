package eu.eventstorm.batch.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eventstorm.batch.BatchAutoConfiguration;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LoggerInstancePostProcessor.class)
@ActiveProfiles("database")
@SpringBootTest(classes = { DatabaseTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(BatchAutoConfiguration.class)
class BatchReactiveControllerTest {

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private DatabaseBatch batch;

	@Test
	void testNoUuid() {
		webClient.get()
			.uri("/batch/123456")
			.exchange()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400)
				.jsonPath("$.title").isEqualTo("DatabaseExecutionNotFoundException")
				.jsonPath("$.params.uuid").isEqualTo("123456")
				.jsonPath("$.traceId").isEqualTo("noTraceId")
				;
	}

	@Test
	void testOk() throws InterruptedException {
		BatchJobCreated bjc = BatchJobCreated.newBuilder()
				.setName("junit-name")
				.setCreatedBy("junit")
				.build();

		batch.push(new EventCandidate<>("junit-stream", "123", bjc));

		Thread.sleep(500);

		webClient.get()
				.uri("/batch/123")
				.exchange()
				.expectBody()
				.jsonPath("$.uuid").isEqualTo("123")
				.jsonPath("$.name").isEqualTo("junit-stream")
				.jsonPath("$.event.name").isEqualTo("junit-name")
				.jsonPath("$.status").isEqualTo("COMPLETED")
				.jsonPath("$.createdBy").isEqualTo("junit")
				;

		webClient.get()
				.uri("/batch/date/2021-01-07")
				.exchange()
				.expectBody()
				.jsonPath("$[0].uuid").isEqualTo("123")
				.jsonPath("$[0].name").isEqualTo("junit-stream")
				.jsonPath("$[0].event.name").isEqualTo("junit-name")
				.jsonPath("$[0].status").isEqualTo("COMPLETED")
				.jsonPath("$[0].createdBy").isEqualTo("junit")
				;

		webClient.get()
				.uri("/batch/today")
				.exchange()
				.expectBody()
				.jsonPath("$[0].uuid").isEqualTo("123")
				.jsonPath("$[0].name").isEqualTo("junit-stream")
				.jsonPath("$[0].event.name").isEqualTo("junit-name")
				.jsonPath("$[0].status").isEqualTo("COMPLETED")
				.jsonPath("$[0].createdBy").isEqualTo("junit")
		;

	}
}
