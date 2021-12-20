package eu.eventstorm.batch.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.justOrEmpty;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Conditional(DatabaseExecutionReactiveControllerCondition.class)
@RestController
public final class DatabaseExecutionReactiveController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseExecutionReactiveController.class);

	private final DatabaseExecutionRepository databaseExecutionRepository;
	private final TransactionTemplate transactionTemplate;
	
	public DatabaseExecutionReactiveController(Database database) {
		this.databaseExecutionRepository = new DatabaseExecutionRepository(database);
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/{uuid}" , produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<DatabaseExecution> getExecution(@PathVariable("uuid") String uuid)  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getExecution({})", uuid);
		}

		return Mono.just(uuid)
				.flatMap(u -> justOrEmpty(transactionTemplate.executeWithReadOnly(() -> databaseExecutionRepository.findById(u))))
				.switchIfEmpty(error(() -> new DatabaseExecutionNotFoundException(uuid)));
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/{uuid}/log" , produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<byte[]> getExecutionLog(@PathVariable("uuid") String uuid)  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getExecutionLog({})", uuid);
		}

		return Mono.just(uuid)
				.flatMap(u -> justOrEmpty(transactionTemplate.executeWithReadOnly(() -> databaseExecutionRepository.findById(u))))
				.switchIfEmpty(error(() -> new DatabaseExecutionNotFoundException(uuid)))
				.map(databaseExecution -> databaseExecution.getLog().write(null));
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/date/{date}", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<DatabaseExecution> getAllByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date)  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAllByDate({})", date);
		}

		return Mono.just(date)
				.flatMapMany(d-> transactionTemplate.flux(() -> databaseExecutionRepository.findAllByDate(d)));
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/today", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<DatabaseExecution> getAllFromToday()  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAllFromToday()");
		}

		return Mono.just(LocalDate.now())
				.flatMapMany(d-> transactionTemplate.flux(() -> databaseExecutionRepository.findAllByDate(d)));
	}

}
