package eu.eventstorm.batch.db;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.protobuf.TypeRegistry;

import eu.eventstorm.batch.rest.CreatedByExtractor;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifierDefinition;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifierGenerator;
import eu.eventstorm.core.uuid.UniversalUniqueIdentifiers;
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

	@Bean
	Database database(DataSource dataSource, TransactionManager transactionManager) {
		return DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.batch.db.Module("eventstorm-batch",""))
		        .build();
	}
	
	@Bean
	TransactionManager eventStormTransactionManager(DataSource dataSource) {
		TransactionManagerConfiguration configuration = new TransactionManagerConfiguration(
		        TransactionTracers.noOp());
		return new TransactionManagerImpl(dataSource, configuration);
	}
	
	@Bean
	CreatedByExtractor createdByExtractor() {
		return request -> "junit";
	}

	@Bean
	UniversalUniqueIdentifierGenerator universalUniqueIdentifierGenerator() {
		return () -> UniversalUniqueIdentifiers.generate(new UniversalUniqueIdentifierDefinition() {
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
	
}