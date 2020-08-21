package eu.eventstorm.batch.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.batch.file.FileResource;

@ActiveProfiles("file")
@SpringBootTest(classes = FileTestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class FileResourceReactiveControllerTest {

	@Autowired
	private WebTestClient webClient;
	
	@Autowired
	private FileResource fileResource;
	
	@Test
	void testUpload() throws IOException {
		EntityExchangeResult<byte[]> result = webClient.post()
			.uri("/toto/upload")
			.body(BodyInserters.fromValue("hello"))
			.exchange()
				.expectBody()
				.returnResult();

		ObjectMapper mapper = new ObjectMapper();
		
		
		@SuppressWarnings("unchecked")
		Map<String,String> response = mapper.readValue(result.getResponseBody(), Map.class);
		
		result = webClient.get()
			.uri("/toto/download/{uuid}", response.get("uuid"))
				.exchange()
			.expectBody()
			.returnResult();
		
		assertEquals("hello", new String(result.getResponseBody()));
		
		fileResource.delete(response.get("uuid"));
		
	}
	
}
