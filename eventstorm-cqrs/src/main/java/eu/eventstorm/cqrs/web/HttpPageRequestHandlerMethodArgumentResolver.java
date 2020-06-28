package eu.eventstorm.cqrs.web;

import static eu.eventstorm.cqrs.util.PageRequests.parse;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import eu.eventstorm.cqrs.QueryDescriptors;
import eu.eventstorm.cqrs.SqlQueryDescriptor;
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
			throw new IllegalStateException("Failed to find class [sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl]", cause);
		}
	}
	
	private final QueryDescriptors queryDescriptors;
	
	private final ConcurrentHashMap<Method, SqlQueryDescriptor> descriptors;
	
	public HttpPageRequestHandlerMethodArgumentResolver(QueryDescriptors queryDescriptors) {
		this.queryDescriptors = queryDescriptors;
		this.descriptors = new ConcurrentHashMap<>();
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		 return parameter.getParameterAnnotation(HttpPageRequest.class) != null;
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
	
		if (PageRequest.class.equals(parameter.getParameter().getType())) {
			return resolveArgument((Method) parameter.getExecutable(), exchange.getRequest().getURI().getQuery());
		}
		
		throw new IllegalStateException();
		
	}
	
	private Mono<Object> resolveArgument(Method method, String uri) {
		
		SqlQueryDescriptor queryDescriptor = this.descriptors.get(method);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("resolveArgument [{}] - [{}] -> [{}]" , uri, queryDescriptor, method);
		}
		
		if (queryDescriptor == null) {
			Object responseEntity = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, method.getGenericReturnType());
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Method ResponseEntity [{}]" , responseEntity);
			}
			
			Object page = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, Array.get(responseEntity, 0));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Method Page [{}]" , page);
			}
			
			Object query = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, Array.get(page, 0));
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Method query [{}]" , query);
			}
			
			Class<?> clazz = (Class<?>)Array.get(query, 0);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Method clazz [{}]" , clazz);
			}
			queryDescriptor = queryDescriptors.getSqlQueryDescriptor(clazz.getName());
			this.descriptors.put(method, queryDescriptor);
		}
		
		return Mono.just(parse(uri, queryDescriptor));
		
	}

}