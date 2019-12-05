package eu.eventstorm.core.eventstore;

import static eu.eventstorm.core.id.AggregateIds.from;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.id.AggregateIds;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class DatabaseEventStoreTest {

	private HikariDataSource ds;
	private Database db;

	@BeforeEach
	void before() throws SQLException, IOException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
		config.setUsername("sa");
		config.setPassword("");

		ds = new HikariDataSource(config);

		try (Connection conn = ds.getConnection()) {
			try (InputStream inputStream = DatabaseEventStoreTest.class.getResourceAsStream("/db/migration/eventstore/h2/V1.0.0__init-schema.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		}

		db = new DatabaseImpl(Dialect.Name.H2, new TransactionManagerImpl(ds), "", new Module("event_store", ""));
	}

	@AfterEach()
	void after() throws SQLException {
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}
	
	@Test
	void testAppend() {
		
		EventStore eventStore = new DatabaseEventStore(db);
		
		eventStore.appendToStream("user", from(1), null);
		eventStore.appendToStream("user", from(1), null);
		
		
		eventStore.readStream("user", from(1)).forEach(System.out::println);
		
	}
}
