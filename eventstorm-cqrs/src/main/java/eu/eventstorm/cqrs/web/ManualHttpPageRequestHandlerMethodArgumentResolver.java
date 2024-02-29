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
public final class ManualHttpPageRequestHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualHttpPageRequestHandlerMethodArgumentResolver.class);

    private final List<PageQueryDescriptors> queryDescriptors;

    private final ConcurrentHashMap<Method, PageQueryDescriptor> descriptors;

    public ManualHttpPageRequestHandlerMethodArgumentResolver(List<PageQueryDescriptors> queryDescriptors) {
        this.queryDescriptors = queryDescriptors;
        this.descriptors = new ConcurrentHashMap<>();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(ManualHttpPageRequest.class) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {

        if (PageRequest.class.equals(parameter.getParameter().getType())) {
            return resolveArgument((Method) parameter.getExecutable(), parameter, exchange.getRequest().getURI().getQuery());
        }

        throw new IllegalStateException();

    }

    private Mono<Object> resolveArgument(Method method, MethodParameter parameter,String uri) {

        PageQueryDescriptor queryDescriptor = this.descriptors.get(method);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resolveArgument [{}] - [{}] -> [{}]", uri, queryDescriptor, method);
        }

        if (queryDescriptor == null) {

            ManualHttpPageRequest request = parameter.getParameterAnnotation(ManualHttpPageRequest.class);

            for (PageQueryDescriptors descriptors : queryDescriptors) {
                queryDescriptor = descriptors.get(request.query().getName());
                if (queryDescriptor != null) {
                    break;
                }
            }

            if (queryDescriptor == null) {
                return Mono.error(new IllegalArgumentException("No PageQueryDescriptors found for class (" + request.query() + ")"));
            }


            this.descriptors.put(method, queryDescriptor);
        }

        return Mono.just(PageRequests.parse(uri, queryDescriptor.getEvaluator()));

    }

}