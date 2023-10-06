package eu.eventstorm.batch.db;

import javax.sql.DataSource;

import eu.eventstorm.batch.rest.HttpRequestMetaExtractor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import com.google.protobuf.TypeRegistry;

import eu.eventstorm.batch.rest.HttpRequestCreatedByExtractor;
import eu.eventstorm.batch.util.HttpHeaderMetaExtractor;
import eu.eventstorm.core.id.StreamIdGenerator;
import eu.eventstorm.core.id.StreamIdGeneratorFactory;
import eu.eventstorm.core.id.UniversalUniqueIdentifierDefinition;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerConfiguration;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.TransactionTracers;


@Profile("database")
@Configuration
@EnableAutoConfiguration
public class DatabaseTestConfiguration {

	@DependsOn("liquibase")
	@Bean
	Database database(TransactionManager transactionManager) {
		return DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.batch.db.Module("eventstorm-batch",""))
				.withModule(new eu.eventstorm.batch.db.QueryViewModule("eventstorm-batch-query",""))
		        .build();
	}
	
	@Bean
	TransactionManager eventStormTransactionManager(DataSource dataSource) {
		TransactionManagerConfiguration configuration = new TransactionManagerConfiguration(
		        TransactionTracers.noOp());
		return new TransactionManagerImpl(dataSource, configuration);
	}
	
	@Profile("database")
	@Bean
    HttpRequestCreatedByExtractor createdByExtractor() {
		return request -> "junit";
	}

	@Profile("database")
	@Bean
	HttpRequestMetaExtractor metaExtractor() {
		return new HttpHeaderMetaExtractor();
	}

	@Bean
	StreamIdGenerator streamIdGenerator() {
		return StreamIdGeneratorFactory.uuid(new UniversalUniqueIdentifierDefinition() {
			public short getRegion() {
				return 0;
			}
			public short getNode() {
				return 0;
			}
		});
	}
	
	@Bean
	TypeRegistry typeRegistry() {
		return TypeRegistry.getEmptyTypeRegistry();
	}
	
	@Bean("junit-name") 
	eu.eventstorm.batch.BatchJob batchJob() {
		return context -> {
			context.log("key_1","value_1");
		};
	}
	
	@Bean("junit-failed") 
	eu.eventstorm.batch.BatchJob batchJobFailed() {
		return context -> {
			throw new RuntimeException("test batch failed");
		};
	}
	
}
