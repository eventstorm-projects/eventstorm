package eu.eventstorm.cqrs.web;

import static eu.eventstorm.cqrs.util.PageRequests.parseQuery;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import eu.eventstorm.cqrs.QueryDescriptors;
import eu.eventstorm.sql.page.PageRequest;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class HttpPageRequestHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpPageRequestHandlerMethodArgumentResolver.class);
	
	private static final Method GET_ACTUAL_TYPE_ARGUMENTS;
	
	static {
		try {
			GET_ACTUAL_TYPE_ARGUMENTS = ReflectionUtils.findMethod(Class.forName("sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl"), "getActualTypeArguments");
		} catch (ClassNotFoundException cause) {
			throw new RuntimeException("Failed to find class [sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl]", cause);
		}
	}
	
	private final QueryDescriptors queryDescriptors;
	
	public HttpPageRequestHandlerMethodArgumentResolver(QueryDescriptors queryDescriptors) {
		this.queryDescriptors = queryDescriptors;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		 return parameter.getParameterAnnotation(HttpPageRequest.class) != null;
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
	
		if (PageRequest.class.equals(parameter.getParameter().getType())) {
			LOGGER.debug("bind SQL PageRequest --> [{}]", parameter);
			return resolveArgument((Method) parameter.getExecutable(), exchange.getRequest().getURI().getQuery());
		}
		
		throw new IllegalStateException();
		
	}
	
	private Mono<Object> resolveArgument(Method method, String uri) {
		Object responseEntity = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, method.getGenericReturnType());
		LOGGER.debug("resolveArgument --> responseEntity --> [{}]", responseEntity);
		LOGGER.debug("resolveArgument --> responseEntity --> [{}]", responseEntity.getClass());
		Object page = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, Array.get(responseEntity, 0));
		LOGGER.debug("resolveArgument --> page --> [{}]", page);
		LOGGER.debug("resolveArgument --> page --> [{}]", page.getClass());
		Object query = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, Array.get(page, 0));
		LOGGER.debug("resolveArgument --> page --> [{}]", query);
		LOGGER.debug("resolveArgument --> page --> [{}]", Array.get(query, 0));
		return Mono.just(parseQuery(uri, queryDescriptors.getSqlQueryDescriptor(((Class<?>)Array.get(query, 0)).getName())));
	}

}