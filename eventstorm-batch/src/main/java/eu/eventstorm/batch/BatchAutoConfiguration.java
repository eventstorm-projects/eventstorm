package eu.eventstorm.batch;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import eu.eventstorm.batch.tmp.TemporaryResourceProperties;

@Configuration
@EnableConfigurationProperties(TemporaryResourceProperties.class)
public class BatchAutoConfiguration {

}
