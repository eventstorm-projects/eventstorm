package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.tracer.Span;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.cqrs.validation.CommandValidationException;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.impl.TransactionDefinitions;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuple2;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static reactor.util.function.Tuples.of;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class LocalDatabaseEventStoreV2CommandHandler<T extends Command> extends LocalDatabaseEventStoreCommandHandler<T> {

    private final Validator<T> consistencyValidator;

    protected LocalDatabaseEventStoreV2CommandHandler(Class<T> type, Validator<T> validator, Validator<T> consistencyValidator) {
        this(type, validator, consistencyValidator,true);
    }

    protected LocalDatabaseEventStoreV2CommandHandler(Class<T> type, Validator<T> validator, Validator<T> consistencyValidator, boolean publish) {
        super(type, validator, publish);
        this.consistencyValidator = consistencyValidator;
    }

    @Override
    protected final void consistencyValidation(CommandContext context, T command) {
        this.consistencyValidator.validate(context,command);
    }

}