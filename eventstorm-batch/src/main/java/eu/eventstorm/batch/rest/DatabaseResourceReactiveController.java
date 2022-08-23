package eu.eventstorm.batch.rest;

import eu.eventstorm.batch.db.DatabaseResource;
import eu.eventstorm.batch.db.DatabaseResourceBuilder;
import eu.eventstorm.batch.db.DatabaseResourceRepository;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.type.Jsons;
import eu.eventstorm.sql.type.common.Lobs;
import eu.eventstorm.sql.util.TransactionTemplate;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.FastByteArrayOutputStream;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Conditional(DatabaseResourceReactiveControllerCondition.class)
@RestController
public final class DatabaseResourceReactiveController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseResourceReactiveController.class);

    private final DatabaseResourceRepository databaseResourceRepository;
    private final TransactionTemplate transactionTemplate;
    private final HttpRequestCreatedByExtractor httpRequestCreatedByExtractor;
    private final HttpRequestMetaExtractor httpRequestMetaExtractor;
    private final StreamIdGenerator generator;

    public DatabaseResourceReactiveController(Database database, StreamIdGenerator generator,
                                              HttpRequestCreatedByExtractor httpRequestCreatedByExtractor,
                                              HttpRequestMetaExtractor httpRequestMetaExtractor) {
        this.databaseResourceRepository = new DatabaseResourceRepository(database);
        this.transactionTemplate = new TransactionTemplate(database.transactionManager());
        this.generator = generator;
        this.httpRequestCreatedByExtractor = httpRequestCreatedByExtractor;
        this.httpRequestMetaExtractor = httpRequestMetaExtractor;
    }

    @PostMapping(path = "${eu.eventstorm.batch.resource.context-path:}/upload")
    public Mono<UploadResponse> upload(ServerHttpRequest serverRequest) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("upload");
        }


        FastByteArrayOutputStream baos = new FastByteArrayOutputStream(32768);

        return DataBufferUtils.write(serverRequest.getBody(), baos)
                .map(DataBufferUtils::release)
                .reduce((l, r) -> l && r)
                .flatMap(t -> Mono.justOrEmpty(this.transactionTemplate.executeWithReadWrite(() -> {
                    String streamId = generator.generate();
                    DatabaseResource br = new DatabaseResourceBuilder()
                            .withId(streamId)
                            .withMeta(Jsons.createMap(httpRequestMetaExtractor.extract(serverRequest)))
                            .withContent(baos.getByteArray())
                            .withCreatedBy(httpRequestCreatedByExtractor.extract(serverRequest))
                            .build();
                    databaseResourceRepository.insert(br);
                    return streamId;
                })))
                .map(UploadResponse::new);
    }

    @GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/list")
    public Flux<DatabaseResourceQuery> list(ServerHttpRequest serverRequest) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("list for [{}]", serverRequest.getQueryParams());
        }

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        serverRequest.getQueryParams().forEach((k, v) -> linkedHashMap.put(k, v.get(0)));

        return this.transactionTemplate.flux(() -> databaseResourceRepository.findByMeta(linkedHashMap, (dialect, rs) -> new DatabaseResourceQuery(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4))));

    }

    @GetMapping(path = "${eu.eventstorm.batch.resource.context-path:}/download/{uuid}")
    public Mono<Void> download(@PathVariable("uuid") String uuid, ServerHttpResponse response) {

        Publisher<? extends DataBuffer> body = Mono
                .fromSupplier(() -> this.transactionTemplate.executeWithReadOnly(() -> databaseResourceRepository.findById(uuid)))
                .flux()
                .flatMap(res -> DataBufferUtils.readInputStream(() -> new FastByteArrayInputStream(res.getContent()), response.bufferFactory(), 2048));

        return response.writeWith(body);
    }

}
