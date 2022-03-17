package eu.eventstorm.eventstore;

import eu.eventstorm.eventstore.rest.ApiRestReadController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EventStoreProperties.class, EventStoreApiProperties.class})
@ComponentScan(basePackageClasses = ApiRestReadController.class)
class EventStoreAutoConfiguration {
	
}
