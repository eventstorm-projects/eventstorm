package eu.eventstorm.saga.init;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaContextListener;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class AuthorizationHeaderSagaContextListener implements SagaContextListener {

    @Override
    public void init(ServerWebExchange exchange, SagaContext context) {
        context.put(HttpHeaders.AUTHORIZATION, exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

}