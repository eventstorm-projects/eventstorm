package eu.eventstorm.sql.spring;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerConfiguration;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tracer.TransactionTracers;

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

	@Bean(destroyMethod = "close")
	DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        DataSource ds = new HikariDataSource(config);

        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(new ClassPathResource("sql/ex001.sql"));

        try (Connection conn = ds.getConnection()) {
            databasePopulator.populate(conn);
        } catch (SQLException cause) {
            cause.printStackTrace();
        }

        return ds;
	}

	@Bean
	TransactionManager eventStormTransactionManager(DataSource ds, Tracer tracer) {
		return new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.brave(tracer)));
	}

	@Bean
	Database database(DataSource ds, TransactionManager transactionManager) {
		return DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.sql.spring.ex001.Module("test", null))
				.build();
	}

}
