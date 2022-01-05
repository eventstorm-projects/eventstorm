package eu.eventstorm.batch.rest;

import eu.eventstorm.batch.db.DatabaseExecutionQuery;
import eu.eventstorm.batch.db.DatabaseExecutionQueryRepository;
import eu.eventstorm.cqrs.web.HttpPageRequest;
import eu.eventstorm.cqrs.web.PageResponseEntity;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.google.common.collect.ImmutableMap.of;
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
	private final DatabaseExecutionQueryRepository databaseExecutionQueryRepository;
	private final TransactionTemplate transactionTemplate;
	
	public DatabaseExecutionReactiveController(Database database) {
		this.databaseExecutionRepository = new DatabaseExecutionRepository(database);
		this.databaseExecutionQueryRepository = new DatabaseExecutionQueryRepository(database);
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/{uuid}" , produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<DatabaseExecutionQuery> getExecution(@PathVariable("uuid") String uuid)  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getExecution({})", uuid);
		}

		return Mono.just(uuid)
				.flatMap(u -> justOrEmpty(transactionTemplate.executeWithReadOnly(() -> databaseExecutionQueryRepository.findById(u))))
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
	public Flux<DatabaseExecutionQuery> getAllByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date)  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAllByDate({})", date);
		}

		return Mono.just(date)
				.flatMapMany(d-> transactionTemplate.flux(() -> databaseExecutionQueryRepository.findAllByDate(d)));
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}/today", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<DatabaseExecutionQuery> getAllFromToday()  {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getAllFromToday()");
		}

		return Mono.just(LocalDate.now())
				.flatMapMany(d-> transactionTemplate.flux(() -> databaseExecutionQueryRepository.findAllByDate(d)));
	}

	@GetMapping(path = "${eu.eventstorm.batch.execution.context-path:}")
	public Mono<ResponseEntity<Page<DatabaseExecutionQuery>>> page(@HttpPageRequest PageRequest pageRequest) {

		return Mono.just(pageRequest)
				.flatMap(p -> Mono.just(this.transactionTemplate.page(() -> databaseExecutionQueryRepository.findBy(p))))
				.map(PageResponseEntity::new);
	}
}
