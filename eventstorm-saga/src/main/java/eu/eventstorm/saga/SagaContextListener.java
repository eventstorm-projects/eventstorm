package eu.eventstorm.saga;

import org.springframework.web.server.ServerWebExchange;

public interface SagaContextListener {

    void init(ServerWebExchange exchange, SagaContext context);

}
