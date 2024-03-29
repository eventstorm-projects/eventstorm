package eu.eventstorm.cqrs.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageImpl;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.Range;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;


import eu.eventstorm.test.LoggerInstancePostProcessor;
import reactor.core.publisher.Mono;

@ExtendWith(LoggerInstancePostProcessor.class)
class HttpPageRequestHandlerMethodArgumentResolverTest {

	@Test
	void testNormal() throws Exception {

		PageQueryDescriptors queryDescriptors = mock(PageQueryDescriptors.class);
		when(queryDescriptors.get("java.lang.String")).thenReturn(mock(PageQueryDescriptor.class));

		HttpPageRequestHandlerMethodArgumentResolver resolver = new HttpPageRequestHandlerMethodArgumentResolver(ImmutableList.of(queryDescriptors));
		MethodParameter methodParameter = new MethodParameter(HttpPageRequestHandlerMethodArgumentResolverTest.class.getMethod("find", PageRequest.class), 0);

		assertTrue(resolver.supportsParameter(methodParameter));

		ServerWebExchange swe = mock(ServerWebExchange.class);
		ServerHttpRequest request = mock(ServerHttpRequest.class);
		when(swe.getRequest()).thenReturn(request);
		when(request.getURI()).thenReturn(URI.create("/one/two?range=0-4"));

		resolver.resolveArgument(methodParameter, new BindingContext(), swe);

		// with cache
		resolver.resolveArgument(methodParameter, new BindingContext(), swe);

	}

	public Mono<ResponseEntity<Page<String>>> find(@HttpPageRequest PageRequest pageRequest) {
		return Mono.just(new PageResponseEntity<>(new PageImpl<>(Stream.of("1", "2", "3", "4", "5"), 20, new Range(0, 4))));
	}
}
