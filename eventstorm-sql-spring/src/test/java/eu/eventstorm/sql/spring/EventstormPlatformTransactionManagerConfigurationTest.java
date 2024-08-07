package eu.eventstorm.sql.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerConfiguration;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.TransactionTracers;
import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class EventstormPlatformTransactionManagerConfigurationTest {

	@Bean
	PlatformTransactionManager transactionManager(TransactionManager transactionManager) {
		return new EventstormPlatformTransactionManager(transactionManager);
	}

	@Bean(destroyMethod = "close")
	HikariDataSource dataSource() throws SQLException{
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

		HikariDataSource ds = new HikariDataSource(config);

        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(new ClassPathResource("sql/ex001.sql"));

        try (Connection conn = ds.getConnection()) {
            databasePopulator.populate(conn);
        }
        return ds;
	}


	@Bean
	Tracer tracer() {
		return Tracer.NOOP;
	}

	@Bean
	TransactionManager eventStormTransactionManager(DataSource ds, Tracer tracer) {
		return new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.micrometer(tracer)));
	}

	@Bean
	Database database(DataSource ds, TransactionManager transactionManager) {
		return DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.sql.spring.ex001.Module("test", null))
				.build();
	}

}
