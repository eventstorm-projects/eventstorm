package eu.eventstorm.batch;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import eu.eventstorm.batch.tmp.TemporaryResourceProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Configuration
@EnableConfigurationProperties(TemporaryResourceProperties.class)
@ComponentScan("eu.eventstorm.batch")
public class BatchAutoConfiguration {

}
