package eu.eventstorm.batch.rest;

import eu.eventstorm.sql.page.PageRequest;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.justOrEmpty;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Conditional(DatabaseResourceReactiveControllerCondition.class)
@RestController
public final class DatabaseExecutionReactiveController {

	private final DatabaseExecutionRepository databaseExecutionRepository;
	private final TransactionTemplate transactionTemplate;
	
	public DatabaseExecutionReactiveController(Database database) {
		this.databaseExecutionRepository = new DatabaseExecutionRepository(database);
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
	}

	@GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/{uuid}")
	public Mono<DatabaseExecution> getExecution(@PathVariable("uuid") String uuid)  {
		return Mono.just(uuid)
				.flatMap(u -> justOrEmpty(transactionTemplate.executeWithReadOnly(() -> databaseExecutionRepository.findById(u))))
				.switchIfEmpty(error(() -> new DatabaseExecutionNotFoundException(uuid)));
	}

}
