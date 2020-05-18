package eu.eventstorm.batch.memory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.batch.rest.TestConfiguration;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.id.StreamIds;
import eu.eventstorm.cqrs.batch.BatchJobCreated;

@SpringBootTest(classes = TestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class InMemoryBatchTest {

	private InMemoryBatch batch = new InMemoryBatch();
	
	@Test
	void push() {
		EventCandidate ev = new EventCandidate("read", StreamIds.from("1234"), BatchJobCreated.newBuilder()
				.setUuid(java.util.UUID.randomUUID().toString())
				.build());
		batch.push(ImmutableList.of(ev));
		
	
	}
}
