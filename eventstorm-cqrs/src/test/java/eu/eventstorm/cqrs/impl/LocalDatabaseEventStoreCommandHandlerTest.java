package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.context.DefaultCommandContext;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.validation.Validators;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { LocalDatabaseEventStoreCommandHandlerConfiguration.class })
@ExtendWith(LoggerInstancePostProcessor.class)
class LocalDatabaseEventStoreCommandHandlerTest {

    @Autowired
    private TestLocalDatabaseEventStoreCommandHandler handler;

    @Autowired
    private TestLocalDatabaseEventStoreFailedCommandHandler handlerFailedValidation;


    @Test
    void testNormal() {
        TestCommand command = new TestCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext(command);

        Flux<Event> flux = handler.handle(commandContext);
        List<Event> events = flux.collectList().block();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(2, command.integer.intValue());
    }

    @Test
    void testFailedValidation() {

        TestFailedCommand command = new TestFailedCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext(command);

        RuntimeException ex =  assertThrows(RuntimeException.class, () -> handlerFailedValidation.handle(commandContext).collectList().block());
        assertEquals("FAILED VALIDATION", ex.getMessage());
        //assertEquals(1, command.integer.get());
    }

    static final class TestCommand implements  Command {
        public final AtomicInteger integer = new AtomicInteger(0);
    }

    static final class TestFailedCommand implements  Command {
        public final AtomicInteger integer = new AtomicInteger(0);
    }

    static final class TestLocalDatabaseEventStoreCommandHandler extends LocalDatabaseEventStoreCommandHandler<TestCommand> {

        public TestLocalDatabaseEventStoreCommandHandler() {
            super(TestCommand.class, Validators.empty());
        }

        @Override
        protected void consistencyValidation(CommandContext context, TestCommand command) {
            assertTrue(Thread.currentThread().getName().startsWith("event-validation-junit"));
            command.integer.incrementAndGet();
        }

        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context, TestCommand command) {
            assertTrue(Thread.currentThread().getName().startsWith("event-loop-junit-"));
            command.integer.incrementAndGet();
            return ImmutableList.of(new EventCandidate<>("test","123", UserCreatedEventPayload.newBuilder().build()));
        }

        @Override
        protected BiConsumer<CommandContext, ImmutableList<Event>> doPostStoreAndEvolution() {
            return (ctx,events) -> {
                assertTrue(Thread.currentThread().getName().startsWith("event-post-junit-"));
            };
        }

    }

    static final class TestLocalDatabaseEventStoreFailedCommandHandler extends LocalDatabaseEventStoreCommandHandler<TestFailedCommand> {

        public TestLocalDatabaseEventStoreFailedCommandHandler() {
            super(TestFailedCommand.class, Validators.empty());
        }

        @Override
        protected void consistencyValidation(CommandContext context, TestFailedCommand command) {
            throw new RuntimeException("FAILED VALIDATION");
        }

        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context, TestFailedCommand command) {
            return null;
        }

        @Override
        protected Consumer<SignalType> onFinally(CommandContext context, TestFailedCommand command) {
            return type -> {
                System.out.println("override onFinally -> " + type + "--> " + Thread.currentThread());
                if (type == SignalType.CANCEL || type == SignalType.ON_ERROR) {
                    command.integer.incrementAndGet();

                }
            };
        }
    }

}
