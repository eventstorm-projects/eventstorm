package eu.eventstorm.saga.init;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaContextListener;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public final class AuthorizationHeaderSagaContextListener implements SagaContextListener {

    @Override
    public void init(ServerWebExchange exchange, SagaContext context) {
        context.put(HttpHeaders.AUTHORIZATION, exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

}