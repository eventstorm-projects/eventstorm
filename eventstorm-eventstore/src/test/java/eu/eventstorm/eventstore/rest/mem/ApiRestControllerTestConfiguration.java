package eu.eventstorm.eventstore.rest.mem;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.TypeRegistry;
import eu.eventstorm.core.json.CoreApiModule;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import eu.eventstorm.eventstore.rest.ApiRestReadController;
import eu.eventstorm.eventstore.rest.db.DatabaseApiRestControllerTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Profile("mem")
@Configuration
@EnableWebFlux
@ComponentScan(basePackageClasses = ApiRestReadController.class)
@EnableAutoConfiguration
class ApiRestControllerTestConfiguration implements WebFluxConfigurer {

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore(new EventStoreProperties());
    }

    @Bean
    StreamManager streamManager() {
        return new InMemoryStreamManagerBuilder()
                .withDefinition("user")
                .withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
                .and()
                .build();
    }

    @Bean
    Scheduler scheduler() {
        return Schedulers.newSingle("event-loop");
    }

    @Bean
    TypeRegistry typeRegistry() {
        return TypeRegistry.newBuilder()
                .add(UserCreatedEventPayload.getDescriptor())
                .build();
    }

    @Bean
    Module coreApiModule(TypeRegistry typeRegistry) {
        return new CoreApiModule(typeRegistry);
    }

}
