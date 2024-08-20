package eu.eventstorm.starter;

import com.fasterxml.jackson.databind.Module;
import com.google.protobuf.TypeRegistry;
import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.core.id.StreamIdGeneratorFactory;
import eu.eventstorm.core.id.UniversalUniqueIdentifierGenerator;
import eu.eventstorm.core.id.UniversalUniqueIdentifiers;
import eu.eventstorm.core.protobuf.DescriptorModule;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.CommandGateway;
import eu.eventstorm.cqrs.CommandHandler;
import eu.eventstorm.cqrs.event.EvolutionHandler;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.tracer.MicrometerTracer;
import eu.eventstorm.cqrs.tracer.NoOpTracer;
import eu.eventstorm.cqrs.web.PageModule;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventbus.NoEventBus;
import eu.eventstorm.problem.ProblemModule;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Configuration
@EnableConfigurationProperties({UniversalUniqueIdentifierDefinitionProperties.class, DatabaseProperties.class, EventStoreProperties.class})
public  class EventStormAutoConfiguration {

    @Bean
    Module problemModule() {
        return new ProblemModule();
    }

    @Bean
    Module pageModule() {
        return new PageModule();
    }

    @Bean
    TypeRegistry typeRegistry(List<DescriptorModule> descriptorModules) {
        Logger logger = LoggerFactory.getLogger(EventStormAutoConfiguration.class);
        TypeRegistry.Builder builder = TypeRegistry.newBuilder();
        descriptorModules.forEach(dm -> {
            logger.info("append DescriptorModule[{}]", dm);
            dm.appendTo(builder);
        });
        return builder.build();
    }

    @ConditionalOnBean(TypeRegistry.class)
    @Bean
    Module cloudEventsModule(TypeRegistry registry) {
        return new CloudEventsModule(registry);
    }

    @Bean
    CommandGateway commandGateway(List<CommandHandler<? extends Command, ?>> handlers) {
        CommandGateway.Builder builder = CommandGateway.newBuilder();
        handlers.forEach(builder::add);
        return builder.build();
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

    @Bean
    CommandGatewayControllerAdvice commandGatewayControllerAdvice() {
        return new CommandGatewayControllerAdvice();
    }

    @ConditionalOnMissingBean
    @Bean
    EventBus eventBus() {
        return new NoEventBus();
    }

    @ConditionalOnProperty(name = "eu.eventstorm.uuid.enabled", havingValue = "true")
    @Bean
    StreamIdGenerator streamIdGenerator(UniversalUniqueIdentifierDefinitionProperties definition) {
        return StreamIdGeneratorFactory.uuid(definition);
    }

    @ConditionalOnProperty(name = "eu.eventstorm.uuid.enabled", havingValue = "true")
    @Bean
    UniversalUniqueIdentifierGenerator universalUniqueIdentifierGenerator(UniversalUniqueIdentifierDefinitionProperties def) {
        return () -> UniversalUniqueIdentifiers.generate(def);
    }

    @Bean
    eu.eventstorm.cqrs.tracer.Tracer eventStoreTracer(EventStoreProperties properties, Optional<Tracer> tracer) {
        LoggerFactory.getLogger(getClass()).info("enabled tracing eventStore -> [{}]", properties.isTracing());
        if (properties.isTracing()) {
            return new MicrometerTracer(tracer.orElseThrow());
        } else {
            return NoOpTracer.INSTANCE;
        }

    }

}