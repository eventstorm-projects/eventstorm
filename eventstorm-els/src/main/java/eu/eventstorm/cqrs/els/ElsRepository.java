package eu.eventstorm.cqrs.els;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ElsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElsRepository.class);

    private final ElasticsearchAsyncClient elasticsearchAsyncClient;

    protected ElsRepository(ElasticsearchAsyncClient elasticsearchAsyncClient) {
        this.elasticsearchAsyncClient = elasticsearchAsyncClient;
    }

    protected final ElasticsearchAsyncClient getElasticsearchAsyncClient() {
        return elasticsearchAsyncClient;
    }

    protected final <T> Mono<T> doFindById(String index, String id, Class<T> clazz) {
        return Mono.fromFuture(this.elasticsearchAsyncClient.get(builder -> builder.index(index).id(id), clazz))
                .flatMap(response -> response.found() ? Mono.just(response.source()) : Mono.empty());
    }

    protected final <T> Mono<IndexResponse> doInsert(String index, String id, T document) {
        return Mono.fromFuture(this.elasticsearchAsyncClient.index(builder -> builder.index(index)
                        .refresh(Refresh.True)
                        .id(id)
                        .document(document)))
                .map(response -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("insert response [{}]", response);
                    }
                    return response;
                });
    }
}