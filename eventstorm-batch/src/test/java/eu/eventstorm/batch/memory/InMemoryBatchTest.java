package eu.eventstorm.batch.memory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import eu.eventstorm.batch.rest.TestConfiguration;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

@SpringBootTest(classes = TestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class InMemoryBatchTest {

	@Autowired
	private InMemoryBatch batch;
	
	@Test
	void push() throws Exception {

		batch.push("read","123", BatchJobCreated.newBuilder()
				.setName("Test")
				.setUuid(java.util.UUID.randomUUID().toString())
				.build());
		
	
		Thread.sleep(10000);
	}
}
