package eu.eventstorm.core.client;

import org.springframework.http.HttpHeaders;

import java.util.function.BiConsumer;

public interface HttpHeadersConsumer<T> extends BiConsumer<HttpHeaders, T> {

}
