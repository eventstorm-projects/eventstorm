package eu.eventstorm.saga.impl;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.saga.SagaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class DefaultCommandGatewayParticipant extends CommandGatewayParticipant {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected DefaultCommandGatewayParticipant(CommandGateway gateway) {
        super(gateway);
    }

    @Override
    public final Mono<SagaContext> execute(SagaContext context) {
        Command command = getCommand(context);
        if (command == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("skip this participant");
            }
            return Mono.just(context);
        }

        fillInContext(context, command);

        if (logger.isDebugEnabled()) {
            logger.debug("Current Saga [{}]", command);
        }
        return executeHandler(command, context).map(c -> context);
    }

    @Override
    public Mono<SagaContext> compensate(SagaContext context) {
        Command originalCommand = getOriginalCommand(context);
        if (originalCommand == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("no command -> skip this compensation");
            }
            return Mono.just(context);
        }
        Command command = getCompensateCommand(context);
        if (logger.isDebugEnabled()) {
            logger.debug("Compensate Saga [{}]", command);
        }
        return executeHandler(command, context).map(c -> context);
    }


    protected abstract Command getCommand(SagaContext context);

    protected abstract Command getOriginalCommand(SagaContext context);

    protected abstract Command getCompensateCommand(SagaContext context);

    protected abstract void fillInContext(SagaContext context, Command command);

}