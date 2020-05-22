package eu.eventstorm.batch.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import eu.eventstorm.batch.BatchExecutor;
import eu.eventstorm.batch.memory.InMemoryBatch;


@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages =  "eu.eventstorm.batch")
public class TestConfiguration {

	@Bean
	InMemoryBatch batch(ApplicationContext applicationContext, BatchExecutor batchExecutor) {
		return new InMemoryBatch(applicationContext, batchExecutor);
	}
	
	@Bean
	com.google.protobuf.TypeRegistry typeRegistry() {
		return com.google.protobuf.TypeRegistry.newBuilder()
				.build();
	}

}