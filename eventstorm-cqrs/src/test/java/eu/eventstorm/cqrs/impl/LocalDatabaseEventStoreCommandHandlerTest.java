package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.core.validation.ConstraintViolation;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { LocalDatabaseEventStoreCommandHandlerConfiguration.class })
@ExtendWith(LoggerInstancePostProcessor.class)
class LocalDatabaseEventStoreCommandHandlerTest {

    @Autowired
    private TestLocalDatabaseEventStoreCommandHandler handler;

    @Test
    void testNormal() {
        TestCommand command = new TestCommand();
        DefaultCommandContext commandContext = new DefaultCommandContext();

        Flux<Event> flux = handler.handle(commandContext, command);
        List<Event> events = flux.collectList().block();

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(2, command.integer.intValue());
    }

    static final class TestCommand implements  Command {
        public final AtomicInteger integer = new AtomicInteger(0);
    }

    static final class TestLocalDatabaseEventStoreCommandHandler extends LocalDatabaseEventStoreCommandHandler<TestCommand> {

        public TestLocalDatabaseEventStoreCommandHandler() {
            super(TestCommand.class, Validators.empty());
        }

        @Override
        protected ImmutableList<ConstraintViolation> consistencyValidation(CommandContext context, TestCommand command) {
            assertTrue(Thread.currentThread().getName().startsWith("main"));
            command.integer.incrementAndGet();
            return ImmutableList.of();
        }

        @Override
        protected ImmutableList<EventCandidate<?>> decision(CommandContext context, TestCommand command) {
            assertTrue(Thread.currentThread().getName().startsWith("event-loop-junit-"));
            command.integer.incrementAndGet();
            return ImmutableList.of(new EventCandidate<>("test","123", UserCreatedEventPayload.newBuilder().build()));
        }

        @Override
        protected void doPostStoreAndEvolution(CommandContext context, ImmutableList<Event> events) {
            assertTrue(Thread.currentThread().getName().startsWith("event-post-junit-"));
        }
    }

}
