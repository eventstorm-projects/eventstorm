package eu.eventstorm.batch.db;

import com.jayway.jsonpath.JsonPath;
import eu.eventstorm.batch.BatchAutoConfiguration;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LoggerInstancePostProcessor.class)
@ActiveProfiles("database")
@SpringBootTest(classes = { DatabaseTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(BatchAutoConfiguration.class)
class DatabaseExecutionReactiveControllerTest {

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private DatabaseBatch batch;

	@Autowired
	private Database database;

	@BeforeEach
	@AfterEach
	void beforeAndAfterEach() {
		try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
			DatabaseExecutionRepository databaseExecutionRepository = new DatabaseExecutionRepository(database);
			databaseExecutionRepository.delete("123");
			databaseExecutionRepository.delete("1234");
			tx.commit();
		}
	}

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
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.uuid").isEqualTo("123")
				.jsonPath("$.name").isEqualTo("junit-stream")
				.jsonPath("$.event.name").isEqualTo("junit-name")
				.jsonPath("$.status").isEqualTo("COMPLETED")
				.jsonPath("$.createdBy").isEqualTo("junit")
				.jsonPath("$.log").doesNotExist()
				;

		webClient.get()
				.uri("/batch/date/{date}", LocalDate.now())
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
				.expectBody()
				.jsonPath("$.uuid").isEqualTo("123")
				.jsonPath("$.name").isEqualTo("junit-stream")
				.jsonPath("$.event.name").isEqualTo("junit-name")
				.jsonPath("$.status").isEqualTo("COMPLETED")
				.jsonPath("$.createdBy").isEqualTo("junit")
				.jsonPath("$.log").doesNotExist()
				;

		webClient.get()
				.uri("/batch/today")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
				.expectBody()
				.jsonPath("$.uuid").isEqualTo("123")
				.jsonPath("$.name").isEqualTo("junit-stream")
				.jsonPath("$.event.name").isEqualTo("junit-name")
				.jsonPath("$.status").isEqualTo("COMPLETED")
				.jsonPath("$.createdBy").isEqualTo("junit")
				.jsonPath("$.log").doesNotExist()
		;


		webClient.get()
				.uri("/batch/123/log")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.key_1").isEqualTo("value_1")
				.jsonPath("$.log").doesNotExist()
		;

		batch.push(new EventCandidate<>("junit-stream-2", "1234", bjc));

		List<String> execs = webClient.get()
				.uri("/batch/today")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
				.returnResult(String.class)
				.getResponseBody()
				.collectList()
				.block();

		assertEquals(2, execs.size());
		assertEquals("1234", JsonPath.parse(execs.get(0)).read("$.uuid"));
		assertEquals("123", JsonPath.parse(execs.get(1)).read("$.uuid"));

	}
}
