package eu.eventstorm.starter;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.core.uuid.UniversalUniqueIdentifierGenerator;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifiers;
import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.CommandHandlerRegistry;
import eu.eventstorm.cqrs.event.EvolutionHandler;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.web.PageModule;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventbus.NoEventBus;
import eu.eventstorm.problem.ProblemModule;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Configuration
@EnableConfigurationProperties(UniversalUniqueIdentifierDefinitionProperties.class)
public class EventStormAutoConfiguration {

	@Bean
	Module problemModule() {
		return new ProblemModule();
	}

	@Bean
	Module pageModule() {
		return new PageModule();
	}

	@ConditionalOnBean(TypeRegistry.class)
	@Bean
	Module cloudEventsModule(TypeRegistry registry) {
		return new CloudEventsModule(registry);
	}

	@Bean
	CommandGateway commandGateway(List<CommandHandler<?, ?>> handlers) {
		CommandHandlerRegistry.Builder builder = CommandHandlerRegistry.newBuilder();
		handlers.forEach(builder::add);
		return new CommandGateway(builder.build());
	}

	@Bean
	EvolutionHandlers evolutionHandlers(List<EvolutionHandler> handlers) {
		EvolutionHandlers.Builder builder = EvolutionHandlers.newBuilder();
		handlers.forEach(builder::add);
		return builder.build();
	}

	@Bean
	CommandValidationRestControllerAdvice commandValidationRestControllerAdvice() {
	    return new CommandValidationRestControllerAdvice();
	}
	
	@ConditionalOnMissingBean(name = "event_store_scheduler")
	@Bean("event_store_scheduler")
	Scheduler eventStoreScheduler() {
		return Schedulers.newSingle("event-loop");
	}

	@Bean 
	UniversalUniqueIdentifierGenerator UniversalUniqueIdentifierGenerator(UniversalUniqueIdentifierDefinitionProperties def) {
		return () -> UniversalUniqueIdentifiers.generate(def);
	}
	
	@ConditionalOnMissingBean
	@Bean
	EventBus eventBus() {
		return new NoEventBus();
	}
	
}