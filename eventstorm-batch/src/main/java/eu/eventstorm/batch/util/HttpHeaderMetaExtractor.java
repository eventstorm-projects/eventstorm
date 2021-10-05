package eu.eventstorm.batch.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eventstorm.batch.rest.HttpRequestMetaExtractor;
import eu.eventstorm.util.Strings;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;

public final class HttpHeaderMetaExtractor implements HttpRequestMetaExtractor {

    private static final String DEFAULT_HEADER = "X-META";

    private final String header;
    private final ObjectMapper objectMapper;

    public HttpHeaderMetaExtractor(String header, ObjectMapper objectMapper) {
        this.header = header;
        this.objectMapper = objectMapper;
    }

    public HttpHeaderMetaExtractor() {
        this(DEFAULT_HEADER, new ObjectMapper());
    }

    @Override
    public Map<String, Object> extract(ServerHttpRequest serverRequest) {
        String meta = serverRequest.getHeaders().getFirst(header);
        if (Strings.isEmpty(meta)) {
            throw new MetaExtractorException(MetaExtractorException.Type.HEADER_NOT_FOUND, of());
        }
        try {
            return this.objectMapper.readValue(meta, Map.class);
        } catch (JsonProcessingException cause) {
            throw new MetaExtractorException(MetaExtractorException.Type.FAILED_TO_PARSE, of("meta", meta), cause);
        }

    }

}
