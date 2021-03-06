package eu.eventstorm.batch.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import eu.eventstorm.sql.Transaction;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import eu.eventstorm.batch.BatchAutoConfiguration;
import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;

@ExtendWith(LoggerInstancePostProcessor.class)
@ActiveProfiles("database")
@SpringBootTest(classes = { DatabaseTestConfiguration.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(BatchAutoConfiguration.class)
class DatabaseBatchTest {

	@Autowired
	private DatabaseBatch batch;
	
	@Autowired
	private Database database;


	@BeforeEach
	@AfterEach
	void beforeAndAfterEach() {
		try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
			DatabaseExecutionRepository databaseExecutionRepository = new DatabaseExecutionRepository(database);
			databaseExecutionRepository.delete("999");
			databaseExecutionRepository.delete("345");
			tx.commit();
		}
	}

	@Test
	void testPushOK() throws InterruptedException {
		
		BatchJobCreated bjc = BatchJobCreated.newBuilder()
				.setName("junit-name")
				.setCreatedBy("junit")
				.build();
		
		batch.push(new EventCandidate<>("junit-stream", "999", bjc));

		Thread.sleep(200);
		
		new TransactionTemplate(database.transactionManager()).executeWithReadOnly(() -> {
			DatabaseExecutionRepository repo = new DatabaseExecutionRepository(database);
			DatabaseExecution dbe = repo.findById("999");
			assertNotNull(dbe);
			assertEquals(BatchStatus.COMPLETED.name(), dbe.getStatus());
		});
	}
	
	@Test
	void testPushAndProcessFailed() throws InterruptedException {
		
		BatchJobCreated bjc = BatchJobCreated.newBuilder()
				.setName("junit-failed")
				.setCreatedBy("junit")
				.build();
		
		batch.push(new EventCandidate<>("junit-stream", "345", bjc));

		Thread.sleep(200);

		new TransactionTemplate(database.transactionManager()).executeWithReadOnly(() -> {
			DatabaseExecutionRepository repo = new DatabaseExecutionRepository(database);
			DatabaseExecution dbe = repo.findById("345");
			assertNotNull(dbe);
			assertEquals(BatchStatus.FAILED.name(), dbe.getStatus());
		});

	}
}
