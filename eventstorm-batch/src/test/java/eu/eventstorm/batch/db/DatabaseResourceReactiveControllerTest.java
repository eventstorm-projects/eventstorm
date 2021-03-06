package eu.eventstorm.batch.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.batch.BatchAutoConfiguration;

@ExtendWith(LoggerInstancePostProcessor.class)
@ActiveProfiles("database")
@SpringBootTest(classes = { DatabaseTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(BatchAutoConfiguration.class)
class DatabaseResourceReactiveControllerTest {

	@Autowired
	private WebTestClient webClient;
	
	@Test
	void testUpload() throws IOException {
		EntityExchangeResult<byte[]> result = webClient.post()
			.uri("/db/upload")
			.header("X-META", "{\"name\":\"test_file.txt\"}")
			.body(BodyInserters.fromValue("helloWorld"))
			.exchange()
				.expectBody()
				.returnResult();

		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String,String> response = mapper.readValue(result.getResponseBody(), Map.class);

		
		result = webClient.get()
			.uri("/db/download/{uuid}", response.get("uuid"))
				.exchange()
			.expectBody()
			.returnResult();
		
		assertEquals("helloWorld", new String(result.getResponseBody()));
		
		
		webClient.get()
				.uri("/db/list?name=test_file.txt")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody()
		       	.jsonPath("$[0].id").isEqualTo(response.get("uuid"))
		       	.jsonPath("$[0].meta.name").isEqualTo("test_file.txt");
			
	}

	@Test
	void testUploadWithMetaFailed() {

		webClient.post()
				.uri("/db/upload")
				.body(BodyInserters.fromValue("helloWorld"))
				.exchange()
				.expectBody()
					.jsonPath("$.status").isEqualTo(400)
					.jsonPath("$.title").isEqualTo("ResourceException")
					.jsonPath("$.traceId").isEqualTo("noTraceId")
				;

		webClient.post()
				.uri("/db/upload")
				.header("X-META", "{\"name\":\"test_file.txt}")
				.body(BodyInserters.fromValue("helloWorld"))
				.exchange()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400)
				.jsonPath("$.title").isEqualTo("ResourceException")
				.jsonPath("$.traceId").isEqualTo("noTraceId")
		;

	}

}
