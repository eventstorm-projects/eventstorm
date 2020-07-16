package eu.eventstorm.eventstore;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EventStoreProperties.class)
public class EventStoreAutoConfiguration {
	
}
