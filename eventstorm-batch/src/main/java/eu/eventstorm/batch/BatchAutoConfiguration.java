package eu.eventstorm.batch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.protobuf.TypeRegistry;

import eu.eventstorm.batch.config.BatchProperties;
import eu.eventstorm.batch.config.ResourceProperties;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.batch.db.DatabaseBatch;
import eu.eventstorm.batch.json.BatchModule;
import eu.eventstorm.batch.memory.InMemoryBatch;
import eu.eventstorm.sql.Database;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Configuration
@EnableConfigurationProperties({ResourceProperties.class, BatchProperties.class})
@ComponentScan("eu.eventstorm.batch")
public class BatchAutoConfiguration {

	@ConditionalOnBean(TypeRegistry.class)
	@Bean
	BatchModule batchModule(TypeRegistry registry) {
		return new BatchModule(registry);
	}

	@Bean
	BatchExecutor batchExecutor(BatchProperties batchProperties) {
		return new BatchExecutor(batchProperties);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "eu.eventstorm.batch", name = "type", havingValue = "MEMORY", matchIfMissing = true)
	Batch batchMemory(ApplicationContext context, BatchExecutor batchExecutor) {
		return new InMemoryBatch(context, batchExecutor);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "eu.eventstorm.batch", name = "type", havingValue = "DATABASE")
	Batch batchDatabase(ApplicationContext context, BatchExecutor batchExecutor, Database database) {
		return new DatabaseBatch(context, batchExecutor, database, new DatabaseExecutionRepository(database));
	}
	
}
