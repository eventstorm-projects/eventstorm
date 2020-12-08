package eu.eventstorm.batch.rest;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConditionalOnProperty(prefix = "eu.eventstorm.batch", name = "type", havingValue = "DATABASE")
@RestController
public final class BatchReactiveController {

	private final DatabaseExecutionRepository databaseExecutionRepository;
	private final TransactionTemplate transactionTemplate;
	private final ObjectMapper objectMapper;
	
	public BatchReactiveController(Database database) {
		this.databaseExecutionRepository = new DatabaseExecutionRepository(database);
		this.objectMapper = new ObjectMapper();
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
	}

	@GetMapping(path = "${eu.eventstorm.batch.context-path:}/{uuid}")
	public Mono<DatabaseExecution> getExecution(@PathVariable("uuid") String uuid) throws IOException {

		DatabaseExecution de = transactionTemplate.executeWithReadOnly(() -> databaseExecutionRepository.findById(uuid));
		
		
		return Mono.empty();

	}
	
}
