package eu.eventstorm.cqrs.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;

import eu.eventstorm.cqrs.QueryDescriptors;
import eu.eventstorm.cqrs.SqlQueryDescriptor;
import eu.eventstorm.sql.page.Page;
import eu.eventstorm.sql.page.PageImpl;
import eu.eventstorm.sql.page.PageRequest;
import eu.eventstorm.sql.page.Range;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import reactor.core.publisher.Mono;

@ExtendWith(LoggerInstancePostProcessor.class)
class HttpPageRequestHandlerMethodArgumentResolverTest {
	
	@Test
	void testNormal() throws Exception {
		
		
		QueryDescriptors queryDescriptors = mock(QueryDescriptors.class);
		when(queryDescriptors.getSqlQueryDescriptor("java.lang.String")).thenReturn(mock(SqlQueryDescriptor.class));
		
		HttpPageRequestHandlerMethodArgumentResolver resolver = new HttpPageRequestHandlerMethodArgumentResolver(queryDescriptors);
		MethodParameter methodParameter = new MethodParameter(HttpPageRequestHandlerMethodArgumentResolverTest.class.getMethod("find", PageRequest.class), 0);
		
		assertTrue(resolver.supportsParameter(methodParameter));
		
		ServerWebExchange swe = mock(ServerWebExchange.class);
		ServerHttpRequest request = mock(ServerHttpRequest.class);
		when(swe.getRequest()).thenReturn(request);
		when(request.getURI()).thenReturn(URI.create("/one/two?range=0-4"));
		
		resolver.resolveArgument(methodParameter, new BindingContext(), swe) ;
		
		// with cache
		resolver.resolveArgument(methodParameter, new BindingContext(), swe) ;

	}

	
	  public Mono<ResponseEntity<Page<String>>> find(@HttpPageRequest PageRequest pageRequest) {
		  return Mono.just(new PageResponseEntity<>(new PageImpl<>(Stream.of("1", "2", "3", "4", "5"), 20, new Range(0, 4))));
	  }
}
