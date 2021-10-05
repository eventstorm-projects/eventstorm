package eu.eventstorm.batch.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eventstorm.batch.rest.HttpRequestMetaExtractor;
import eu.eventstorm.util.Strings;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;

public final class HttpQueryParamMetaExtractor implements HttpRequestMetaExtractor {

    private static final String DEFAULT_HEADER = "meta";

    private final String param;
    private final ObjectMapper objectMapper;

    public HttpQueryParamMetaExtractor(String param, ObjectMapper objectMapper) {
        this.param = param;
        this.objectMapper = objectMapper;
    }

    public HttpQueryParamMetaExtractor() {
        this(DEFAULT_HEADER, new ObjectMapper());
    }

    @Override
    public Map<String, Object> extract(ServerHttpRequest serverRequest) {
        String meta = serverRequest.getQueryParams().getFirst(param);
        if (Strings.isEmpty(meta)) {
            throw new MetaExtractorException(MetaExtractorException.Type.QUERY_PARAM_NOT_FOUND, of());
        }
        try {
            return this.objectMapper.readValue(Base64.getDecoder().decode(meta), Map.class);
        } catch (IOException cause) {
            throw new MetaExtractorException(MetaExtractorException.Type.FAILED_TO_PARSE, of("meta", meta), cause);
        }

    }

}
