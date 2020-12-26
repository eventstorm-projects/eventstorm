package eu.eventstorm.batch.rest;

import java.io.IOException;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
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

import eu.eventstorm.batch.db.DatabaseResource;
import eu.eventstorm.batch.db.DatabaseResourceBuilder;
import eu.eventstorm.batch.db.DatabaseResourceRepository;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.sql.type.Jsons;
import eu.eventstorm.sql.type.common.Blobs;
import eu.eventstorm.sql.util.TransactionTemplate;
import eu.eventstorm.util.FastByteArrayOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Conditional(DatabaseResourceReactiveControllerCondition.class)
@RestController
public final class DatabaseResourceReactiveController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseResourceReactiveController.class);
	
	private final DatabaseResourceRepository databaseResourceRepository;
	private final ObjectMapper objectMapper;
	private final TransactionTemplate transactionTemplate;
	private final CreatedByExtractor createdByExtractor;
	private final StreamIdGenerator generator;
	
	public DatabaseResourceReactiveController(Database database, CreatedByExtractor createdByExtractor, StreamIdGenerator generator) {
		this.databaseResourceRepository = new DatabaseResourceRepository(database);
		this.objectMapper = new ObjectMapper();
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
		this.createdByExtractor = createdByExtractor;
		this.generator = generator;
	}

	@PostMapping(path = "${eu.eventstorm.batch.resource.context-path:}/upload")
	public Mono<UploadResponse> upload(ServerHttpRequest serverRequest) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("upload");
		}
		
		String streamId = generator.generate();
		FastByteArrayOutputStream baos = new FastByteArrayOutputStream(32768);

		return DataBufferUtils.write(serverRequest.getBody(), baos)
				.map(DataBufferUtils::release)
				.reduce((l, r) -> l && r)
				.doOnNext(t -> this.transactionTemplate.executeWithReadWrite(() -> {
					Json meta = getMeta(serverRequest);
					DatabaseResource br = new DatabaseResourceBuilder()
							.withId(streamId)
							.withMeta(meta)
							.withContent(Blobs.newBlob(baos))
							.withCreatedBy(createdByExtractor.extract(serverRequest))
							.build();
					databaseResourceRepository.insert(br);
					return null;
				}))
				.map(result -> new UploadResponse(streamId));
	}
	
	@GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/list")
	public Flux<DatabaseResourceQuery> list(ServerHttpRequest serverRequest) throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("list for [{}]", serverRequest.getQueryParams());
		}
		
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		serverRequest.getQueryParams().forEach((k,v) -> builder.put(k, v.get(0)));
		
		return this.transactionTemplate.flux(() -> databaseResourceRepository.findByMeta(builder.build(), (dialect, rs) -> new DatabaseResourceQuery(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4))));

	}
	
	@GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/download/{uuid}")
	public Mono<Void> download(@PathVariable("uuid") String uuid, ServerHttpResponse response) {

		Publisher<? extends DataBuffer> body = Mono
				.fromSupplier(() -> this.transactionTemplate.executeWithReadOnly(() ->  databaseResourceRepository.findById(uuid)))
				.flux()
				.flatMap(res -> DataBufferUtils.readInputStream(() -> res.getContent().getBinaryStream(), response.bufferFactory(), 2048));

		return response.writeWith(body);
	}

	@SuppressWarnings("unchecked")
	private Json getMeta(ServerHttpRequest serverRequest) {
		String meta = serverRequest.getHeaders().getFirst("X-META");
		try {
			return Jsons.createMap(this.objectMapper.readValue(meta, Map.class));
		} catch (JsonProcessingException cause) {
			throw new ResourceException(ResourceException.Type.CONVERT_ERROR, ImmutableMap.of("meta",meta), cause);
		}
	}
	
}
