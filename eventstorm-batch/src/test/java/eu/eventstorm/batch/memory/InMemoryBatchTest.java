package eu.eventstorm.batch.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import com.google.protobuf.ByteString;

import eu.eventstorm.batch.rest.FileTestConfiguration;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

@ActiveProfiles("file")
@SpringBootTest(classes = FileTestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class InMemoryBatchTest {

	@Autowired
	private InMemoryBatch batch;
	
	@Autowired
	private TestBatchJob job;
	
	@Test
	void push() throws Exception {

		batch.push(new EventCandidate<BatchJobCreated>("read","123", BatchJobCreated.newBuilder()
				.setName("Test")
				.setCommand(ByteString.copyFrom("[{\"uuid\":\""+ java.util.UUID.randomUUID() +"\"}]", StandardCharsets.UTF_8))
				.build()));
		
		batch.push(new EventCandidate<BatchJobCreated>("read","456", BatchJobCreated.newBuilder()
				.setName("Test")
				.setCommand(ByteString.copyFrom("[{\"uuid\":\""+ java.util.UUID.randomUUID() +"\"}]", StandardCharsets.UTF_8))
				.build()));
		
		batch.push(new EventCandidate<BatchJobCreated>("read","789", BatchJobCreated.newBuilder()
				.setName("Test")
				.setCommand(ByteString.copyFrom("[{\"uuid\":\""+ java.util.UUID.randomUUID() +"\"}]", StandardCharsets.UTF_8))
				.build()));
		
		Thread.sleep(1500);
		assertEquals(3, job.counter.get());
		
	}
}
