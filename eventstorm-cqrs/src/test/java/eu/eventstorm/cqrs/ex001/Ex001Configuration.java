package eu.eventstorm.cqrs.ex001;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.CommandHandlerRegistry;
import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.cqrs.ex001.gen.evolution.UserEvolutionHandler;
import eu.eventstorm.cqrs.impl.EventLoops;
import eu.eventstorm.eventstore.EventStoreClient;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;
import eu.eventstorm.eventstore.memory.InMemoryEventStoreClient;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import reactor.core.scheduler.Schedulers;

@Configuration
@ComponentScan("eu.eventstorm.cqrs.ex001.handler")
class Ex001Configuration {
	
	@Bean
	StreamManager streamManager() {
		return new InMemoryStreamManagerBuilder()
			.withDefinition("user")
				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
			.and()
			.build();
	}
	
	@Bean
	InMemoryEventStoreClient eventStoreClient(StreamManager manager) {
		return new InMemoryEventStoreClient(manager, new InMemoryEventStore(new EventStoreProperties()));
	}
	
	@Bean
	EvolutionHandlers evolutionHandlers() { 
		return EvolutionHandlers.newBuilder()
				.add(new UserEvolutionHandler())
				.build();
	}
			
	@Bean 
	CommandGateway gateway(EventStoreClient eventStoreClient, EvolutionHandlers evolutionHandlers, List<CommandHandler<? extends Command, ?>> commands) {
		CommandHandlerRegistry.Builder registry = CommandHandlerRegistry.newBuilder();
		commands.forEach(registry::add);
		return new CommandGateway(registry.build());
	}
	
	@Bean
	EventLoop eventLoop() {
		return EventLoops.single(Schedulers.newSingle("event-loop-junit"));
	}
}
