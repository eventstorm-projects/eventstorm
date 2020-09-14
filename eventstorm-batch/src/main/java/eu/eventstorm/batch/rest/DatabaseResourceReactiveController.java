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

import eu.eventstorm.batch.db.DatabaseResource;
import eu.eventstorm.batch.db.DatabaseResourceBuilder;
import eu.eventstorm.batch.db.DatabaseResourceRepository;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifier;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifierGenerator;
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
public final class DatabaseResourceReactiveController {

	private final DatabaseResourceRepository databaseResourceRepository;
	private final ObjectMapper objectMapper;
	private final TransactionTemplate transactionTemplate;
	private final CreatedByExtractor createdByExtractor;
	private final UniversalUniqueIdentifierGenerator generator;
	
	public DatabaseResourceReactiveController(Database database, CreatedByExtractor createdByExtractor, UniversalUniqueIdentifierGenerator generator) {
		this.databaseResourceRepository = new DatabaseResourceRepository(database);
		this.objectMapper = new ObjectMapper();
		this.transactionTemplate = new TransactionTemplate(database.transactionManager());
		this.createdByExtractor = createdByExtractor;
		this.generator = generator;
	}

	@PostMapping(path = "${eu.eventstorm.batch.resource.context-path:}/upload")
	public Mono<UploadResponse> upload(ServerHttpRequest serverRequest) throws IOException {

		UniversalUniqueIdentifier uuid = generator.generate();
		FastByteArrayOutputStream baos = new FastByteArrayOutputStream(32768);

		return DataBufferUtils.write(serverRequest.getBody(), baos)
				.map(DataBufferUtils::release)
				.reduce((l, r) -> l && r)
				.doOnNext(t -> {
					this.transactionTemplate.executeWithReadWrite(() -> {
						Json meta = getMeta(serverRequest);
						DatabaseResource br = new DatabaseResourceBuilder()
								.withId(uuid.toString())
								.withMeta(meta)
								.withContent(Blobs.newBlob(baos))
								.withCreatedBy(createdByExtractor.extract(serverRequest))
								.build();
						databaseResourceRepository.insert(br);
						return null;
					});
				})
				.map(result -> new UploadResponse(uuid.toString()));

	}
	
	@GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/download/{uuid}")
	public Mono<Void> download(@PathVariable("uuid") String uuid, ServerHttpResponse response) throws IOException {

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
