package eu.eventstorm.cqrs.els;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonpUtils;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.cqrs.els.page.ElsFilterVisitor;
import eu.eventstorm.cqrs.els.page.ElsPageRequestDescriptor;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageImpl;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class ElsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElsRepository.class);

    private final ElasticsearchAsyncClient elasticsearchAsyncClient;
    private final PageQueryDescriptors pageQueryDescriptors;
    private final Function<String, String> indexResolver;

    protected ElsRepository(ElasticsearchAsyncClient elasticsearchAsyncClient, PageQueryDescriptors pageQueryDescriptors) {
        this(elasticsearchAsyncClient, pageQueryDescriptors, index -> index);
    }

    protected ElsRepository(ElasticsearchAsyncClient elasticsearchAsyncClient, PageQueryDescriptors pageQueryDescriptors, Function<String, String> indexResolver) {
        this.elasticsearchAsyncClient = elasticsearchAsyncClient;
        this.pageQueryDescriptors = pageQueryDescriptors;
        this.indexResolver = indexResolver;
    }

    protected final ElasticsearchAsyncClient getElasticsearchAsyncClient() {
        return elasticsearchAsyncClient;
    }

    protected final <T> Mono<T> doFindById(String index, String id, Class<T> clazz) {
        return Mono.fromFuture(this.elasticsearchAsyncClient.get(builder -> builder
                        .index(indexResolver.apply(index))
                        .id(id), clazz))
                .flatMap(response -> response.found() ? Mono.just(response.source()) : Mono.empty());
    }

    protected final <T> Mono<IndexResponse> doInsert(String index, String id, T document) {
        return Mono.fromFuture(this.elasticsearchAsyncClient.index(builder -> builder
                        .index(indexResolver.apply(index))
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

    protected final <T> Mono<UpdateResponse<T>> doUpdate(String index, String id, T document, Class<T> clazz) {

        return Mono.fromFuture(this.elasticsearchAsyncClient.update(new UpdateRequest.Builder<T, T>()
                        .index(indexResolver.apply(index))
                        .id(id)
                        .doc(document)
                        .refresh(Refresh.True)
                        .build(), clazz))
                .map(response -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("partial update response [{}]", response);
                    }
                    return response;
                });
    }

    protected final <T, E> Mono<UpdateResponse<T>> doPartialUpdate(String index, String id, E document, Class<T> clazz) {
        return Mono.fromFuture(this.elasticsearchAsyncClient.update(new UpdateRequest.Builder<T, E>()
                        .index(index)
                        .id(id)
                        .doc(document)
                        .refresh(Refresh.True)
                        .build(), clazz))
                .map(response -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("update response [{}]", response);
                    }
                    return response;
                });
    }

    protected final <T> Mono<Page<T>> doSelectPage(String index, PageRequest pageRequest, Class<T> clazz) {

        ElsFilterVisitor visitor = new ElsFilterVisitor((ElsPageRequestDescriptor) pageQueryDescriptors.get(clazz.getName()));
        pageRequest.getFilter().accept(visitor);

        SearchRequest.Builder builder = new SearchRequest.Builder()
                .index(indexResolver.apply(index))
                .query(visitor.getQuery())
                .size(pageRequest.getSize())
                .from(pageRequest.getOffset());

        SearchRequest request = builder.build();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executeSelectPage -> query =[{}]", JsonpUtils.toString(request));
        }

        return Mono.fromFuture(this.elasticsearchAsyncClient.search(request, clazz))
                .map(response -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("executeSelectPage -> find count=[{}]", response.hits().total().value());
                    }

                    return new PageImpl<>(response.hits().hits().stream().map(Hit::source), response.hits().total().value(),
                            new Range(pageRequest.getOffset(), pageRequest.getOffset() + pageRequest.getSize() - 1));

                });

    }

}