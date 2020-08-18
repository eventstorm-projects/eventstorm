package eu.eventstorm.batch.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import eu.eventstorm.batch.rest.TestConfiguration;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

@SpringBootTest(classes = TestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class InMemoryBatchTest {

	@Autowired
	private InMemoryBatch batch;
	
	@Autowired
	private TestBatchJob job;
	
	@Test
	void push() throws Exception {

		batch.push(new EventCandidate<BatchJobCreated>("read",StreamIds.from("123"), BatchJobCreated.newBuilder()
				.setName("Test")
				.addUuid(java.util.UUID.randomUUID().toString())
				.build()));
		
		batch.push(new EventCandidate<BatchJobCreated>("read",StreamIds.from("456"), BatchJobCreated.newBuilder()
				.setName("Test")
				.addUuid(java.util.UUID.randomUUID().toString())
				.build()));
		
		batch.push(new EventCandidate<BatchJobCreated>("read",StreamIds.from("789"), BatchJobCreated.newBuilder()
				.setName("Test")
				.addUuid(java.util.UUID.randomUUID().toString())
				.build()));
		
		Thread.sleep(1500);
		assertEquals(3, job.counter.get());
		
	}
}
