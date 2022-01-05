package eu.eventstorm.batch;

import eu.eventstorm.annotation.CqrsConfiguration;
import eu.eventstorm.batch.config.BatchExecutionProperties;
import eu.eventstorm.batch.db.json.QueryModule;
import eu.eventstorm.batch.file.FileResource;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.cqrs.web.HttpPageRequestHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.batch.config.BatchProperties;
import eu.eventstorm.batch.config.ResourceProperties;
import eu.eventstorm.batch.db.DatabaseBatch;
import eu.eventstorm.batch.db.DatabaseExecutionRepository;
import eu.eventstorm.batch.json.BatchModule;
import eu.eventstorm.batch.memory.InMemoryBatch;
import eu.eventstorm.core.protobuf.DescriptorModule;
import eu.eventstorm.cqrs.batch.BatchJobCreated;
import eu.eventstorm.sql.Database;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Configuration
@CqrsConfiguration(basePackage = "eu.eventstorm.batch", id = "eventstormBatch")
@EnableConfigurationProperties({ResourceProperties.class, BatchProperties.class, BatchExecutionProperties.class})
@ComponentScan("eu.eventstorm.batch")
public class BatchAutoConfiguration implements WebFluxConfigurer {

	private final Optional<List<PageQueryDescriptors>> pageQueryDescriptors;

	public BatchAutoConfiguration(Optional<List<PageQueryDescriptors>> pageQueryDescriptors) {
		this.pageQueryDescriptors = pageQueryDescriptors;
	}

	@Bean
	BatchModule batchModule() {
		return new BatchModule();
	}

	@Bean
	DescriptorModule batchDescriptorModule() {
		return new DescriptorModule("eventstorm-batch", ImmutableList.of(BatchJobCreated.getDescriptor()));
	}
	
	@ConditionalOnBean(TypeRegistry.class)
	@Bean
	JsonFormat.Parser eventJsonParser(TypeRegistry registry) {
		return JsonFormat.parser().usingTypeRegistry(registry);
	}

	@Bean
	BatchExecutor batchExecutor(BatchProperties batchProperties) {
		return new BatchExecutor(batchProperties);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "eu.eventstorm.batch", name = "type", havingValue = "MEMORY", matchIfMissing = true)
	Batch batchMemory(ApplicationContext context, BatchExecutor batchExecutor, FileResource fileResource) {
		return new InMemoryBatch(context, batchExecutor, fileResource);
	}
	
	@Bean
	@ConditionalOnProperty(prefix = "eu.eventstorm.batch", name = "type", havingValue = "DATABASE")
	Batch batchDatabase(ApplicationContext context, BatchExecutor batchExecutor, Database database, TypeRegistry registry) {
		return new DatabaseBatch(context, batchExecutor, database, new DatabaseExecutionRepository(database), registry);
	}

	@ConditionalOnProperty(prefix = "eu.eventstorm.batch.resource", name = "rest-enabled", havingValue = "true")
	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		configurer.addCustomResolver(new HttpPageRequestHandlerMethodArgumentResolver(pageQueryDescriptors.get()));
	}
}
