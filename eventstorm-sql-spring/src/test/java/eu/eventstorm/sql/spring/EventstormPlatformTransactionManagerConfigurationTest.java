package eu.eventstorm.sql.spring;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.tx.TransactionManager;
import eu.eventstorm.sql.tx.TransactionManagerConfiguration;
import eu.eventstorm.sql.tx.TransactionManagerImpl;
import eu.eventstorm.sql.tx.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tx.tracer.TransactionTracers;

@Configuration
public class EventstormPlatformTransactionManagerConfigurationTest {

	@Bean
	PlatformTransactionManager transactionManager(TransactionManager transactionManager) {
		return new EventstormPlatformTransactionManager(transactionManager);
	}
	
	@Bean
	Tracer tracer() {
		return Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).spanReporter(new LoggingBraveReporter()).build().tracer();
	}
	
	@Bean
	DataSource dataSource() {
		return JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
	}
	
	@Bean
	TransactionManager eventStormTransactionManager(DataSource ds, Tracer tracer) {
		return new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.brave(tracer)));
	}
	
	@Bean
	Database database(DataSource ds, TransactionManager transactionManager) {
		return new DatabaseImpl(ds, Dialect.Name.H2, transactionManager, "", new eu.eventstorm.sql.spring.ex001.Module("test", null));

	}
	
}
