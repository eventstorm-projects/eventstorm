package eu.eventstorm.saga;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface SagaContextListener {

    void init(ServerWebExchange exchange, SagaContext context);

}
