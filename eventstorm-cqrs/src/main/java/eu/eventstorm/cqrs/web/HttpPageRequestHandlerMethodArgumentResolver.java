package eu.eventstorm.cqrs.web;

import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.PageRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    private final List<PageQueryDescriptors> queryDescriptors;

    private final ConcurrentHashMap<Method, PageQueryDescriptor> descriptors;

    public HttpPageRequestHandlerMethodArgumentResolver(List<PageQueryDescriptors> queryDescriptors) {
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

        PageQueryDescriptor queryDescriptor = this.descriptors.get(method);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolveArgument [{}] - [{}] -> [{}]", uri, queryDescriptor, method);
        }

        if (queryDescriptor == null) {
            Object responseEntity = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, method.getGenericReturnType());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Method ResponseEntity [{}]", responseEntity);
            }

            Object returnType = Array.get(responseEntity, 0);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("returnType [{}]", returnType);
            }

            if (returnType instanceof Class && "java.lang.Void".equals(((Class) returnType).getName())) {
                queryDescriptor = () -> (operator, values) -> values;
                this.descriptors.put(method, queryDescriptor);
                return Mono.just(PageRequests.parse(uri, queryDescriptor.getEvaluator()));
            }

            Object page = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, returnType);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Method Page [{}]", page);
            }

            if (page == null) {
                return Mono.error(new IllegalArgumentException("No Type Argument found for the Page (" + returnType + ")"));
            }

            if ("java.lang.Void".equals(page.toString())) {
                return Mono.empty();
            }

            Object query = ReflectionUtils.invokeMethod(GET_ACTUAL_TYPE_ARGUMENTS, Array.get(page, 0));

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Method query [{}]", query);
            }

            Class<?> clazz = (Class<?>) Array.get(query, 0);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Method clazz [{}]", clazz);
            }

            for (PageQueryDescriptors descriptors : queryDescriptors) {
                queryDescriptor = descriptors.get(clazz.getName());
                if (queryDescriptor != null) {
                    break;
                }
            }

            if (queryDescriptor == null) {
                return Mono.error(new IllegalArgumentException("No PageQueryDescriptors found for class (" + clazz + ")"));
            }

            this.descriptors.put(method, queryDescriptor);
        }

        return Mono.just(PageRequests.parse(uri, queryDescriptor.getEvaluator()));

    }

}