package eu.eventstorm.batch.rest;

import java.io.IOException;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.batch.db.DatabaseResource;
import eu.eventstorm.batch.db.DatabaseResourceBuilder;
import eu.eventstorm.batch.db.DatabaseResourceRepository;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Jsons;
import eu.eventstorm.sql.type.common.Blobs;
import eu.eventstorm.sql.util.TransactionTemplate;
import eu.eventstorm.util.FastByteArrayOutputStream;
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
	public Mono<DatabaseExecution> upload(@PathVariable("uuid") String uuid) throws IOException {

		return Mono.empty();

	}
	
}
