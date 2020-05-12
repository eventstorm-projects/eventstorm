package eu.eventstorm.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.google.protobuf.TypeRegistry;

import eu.eventstorm.cloudevents.json.jackson.CloudEventsModule;
import eu.eventstorm.problem.ProblemModule;

@Configuration
public class EventStormAutoConfiguration {

	@Bean
	Module problemModule() {
		return new ProblemModule();
	}
	
	@Bean
	Module cloudEventsModule(TypeRegistry registry) {
		return new CloudEventsModule(registry);
	}
	
	@Bean
	CommandValidationRestControllerAdvice commandValidationRestControllerAdvice() {
	    return new CommandValidationRestControllerAdvice();
	}
	
}