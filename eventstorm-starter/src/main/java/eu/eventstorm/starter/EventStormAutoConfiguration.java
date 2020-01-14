package eu.eventstorm.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import eu.eventstorm.core.json.jackson.CloudEventsModule;
import eu.eventstorm.problem.ProblemModule;

@Configuration
public class EventStormAutoConfiguration {

	@Bean
	Module problemModule() {
		return new ProblemModule();
	}
	
	@Bean
	Module cloudEventsModule() {
		return new CloudEventsModule();
	}
	
	@Bean
	CommandValidationRestControllerAdvice commandValidationRestControllerAdvice() {
	    return new CommandValidationRestControllerAdvice();
	}
	
}