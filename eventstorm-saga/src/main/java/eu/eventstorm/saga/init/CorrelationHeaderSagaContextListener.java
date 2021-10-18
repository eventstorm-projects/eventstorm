package eu.eventstorm.saga.init;

import eu.eventstorm.saga.SagaContext;
import eu.eventstorm.saga.SagaContextListener;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CorrelationHeaderSagaContextListener implements SagaContextListener {

    private static final String HEADER_CORRELATION = "X-SAGA-CORRELATION";

    @Override
    public void init(ServerWebExchange exchange, SagaContext context) {
        context.put(HEADER_CORRELATION, context.getUniversalUniqueIdentifier().toString());
    }

}