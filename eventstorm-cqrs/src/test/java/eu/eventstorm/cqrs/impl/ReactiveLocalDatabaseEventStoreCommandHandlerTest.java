package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.context.DefaultCommandContext;
import eu.eventstorm.cqrs.validation.ReactiveValidator;
import eu.eventstorm.cqrs.validation.ReactiveValidators;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ReactiveLocalDatabaseEventStoreCommandHandlerConfiguration.class })
@ExtendWith(LoggerInstancePostProcessor.class)
class ReactiveLocalDatabaseEventStoreCommandHandlerTest {


    @Autowired
    private TestReactiveLocalDatabaseEventStoreFailedOnContractCommandHandler handlerFailedValidation;

    @Autowired
    private TestReactiveLocalDatabaseEventStoreFailedOnConsistencyCommandHandler failedOnConsistencyCommandHandler;

    @Autowired
    private TestReactiveLocalDatabaseEventStoreCommandHandler handler;


    @Test
    void testNormal() {
        TestCommand command = new TestCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext(command);

        Flux<Event> flux = handler.handle(commandContext);
        List<Event> events = flux.collectList().block();

        assertNotNull(events);
       // assertEquals(1, events.size());
        //assertEquals(2, command.integer.intValue());
    }

    @Test
    void testFailedValidation() {

        TestFailedCommand command = new TestFailedCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext(command);

        RuntimeException ex =  assertThrows(RuntimeException.class, () -> handlerFailedValidation.handle(commandContext).blockLast());
        assertEquals("FAILED VALIDATION", ex.getMessage());
        //assertEquals(1, command.integer.get());
    }

    @Test
    void testFailedOnConsistencyCommandHandler() {

        TestFailedCommand command = new TestFailedCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext(command);

        RuntimeException ex =  assertThrows(RuntimeException.class, () -> failedOnConsistencyCommandHandler.handle(commandContext).collectList().block());
        assertEquals("FAILED VALIDATION", ex.getMessage());
        //assertEquals(1, command.integer.get());
    }

    static final class TestCommand implements  Command {
        public final AtomicInteger integer = new AtomicInteger(0);
    }

    static final class TestFailedCommand implements  Command {
        public final AtomicInteger integer = new AtomicInteger(0);
    }

    @Component
    static final class TestReactiveLocalDatabaseEventStoreCommandHandler extends ReactiveLocalDatabaseEventStoreCommandHandler<TestCommand> {

        public TestReactiveLocalDatabaseEventStoreCommandHandler() {
            super(TestCommand.class, ReactiveValidators.empty(), ReactiveValidators.empty());
        }

        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context) {
            return ImmutableList.of();
        }
    }


    @Component
    static final class TestReactiveLocalDatabaseEventStoreFailedOnContractCommandHandler extends ReactiveLocalDatabaseEventStoreCommandHandler<TestFailedCommand> {
        public TestReactiveLocalDatabaseEventStoreFailedOnContractCommandHandler() {
            super(TestFailedCommand.class, exception(), exception());
        }
        private static ReactiveValidator exception() {
            return ctx -> Mono.error(new RuntimeException("FAILED VALIDATION"));

        }
        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context) {
            return ImmutableList.of();
        }
    }

    @Component
    static final class TestReactiveLocalDatabaseEventStoreFailedOnConsistencyCommandHandler extends ReactiveLocalDatabaseEventStoreCommandHandler<TestFailedCommand> {
        public TestReactiveLocalDatabaseEventStoreFailedOnConsistencyCommandHandler() {
            super(TestFailedCommand.class, ReactiveValidators.empty(), exception());
        }
        private static ReactiveValidator exception() {
            return ctx -> Mono.error(new RuntimeException("FAILED VALIDATION"));
        }
        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context) {
            return ImmutableList.of();
        }
    }

}
