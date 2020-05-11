package eu.eventstorm.eventstore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.memory.InMemoryEventStore;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
@ComponentScan(basePackageClasses = ApiRestController.class)
@EnableWebFlux
@EnableAutoConfiguration
class ApiRestControllerConfigurationTest implements WebFluxConfigurer {

	
	@Autowired
	ObjectMapper objectMapper;

    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(objectMapper)
        );

        configurer.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(objectMapper)
        );
    }
    
//	@Bean
//	EventPayloadRegistry registry() {
//		EventPayloadRegistryBuilder builder = new EventPayloadRegistryBuilder();
//		builder.add(UserCreatedEventPayload.class, new UserCreatedEventPayloadSerializer(new ObjectMapper()), new UserCreatedEventPayloadDeserializer());
//		return builder.build();
//	}
	
	@Bean
	EventStore eventStore() {
		return new InMemoryEventStore();
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
	Module coreApiModule() {
		return new CoreApiModule();
	}
	
}
