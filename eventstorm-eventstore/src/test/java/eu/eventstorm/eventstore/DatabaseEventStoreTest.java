package eu.eventstorm.eventstore;

import static eu.eventstorm.core.id.AggregateIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.eventstore.db.DatabaseEventStore;
import eu.eventstorm.eventstore.db.Module;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayloadDeserializer;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayloadImpl;
import eu.eventstorm.eventstore.registry.EventPayloadRegistryBuilder;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
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
		
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModule(new Module("event_store", ""))
				.build();
	}

	@AfterEach()
	void after() throws SQLException {
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}
	
	@Test
	void testAppend() {
		
		EventPayloadRegistryBuilder builder = new EventPayloadRegistryBuilder();
		builder.add(UserCreatedEventPayload.class, null, new UserCreatedEventPayloadDeserializer());
		
		EventStore eventStore = new DatabaseEventStore(db,builder.build());
		
		
		eventStore.appendToStream("user", from(1), new UserCreatedEventPayloadImpl("ja","gmail",39));
		eventStore.appendToStream("user", from(1), new UserCreatedEventPayloadImpl("ja","gmail",39));
		
		try (Stream<Event<EventPayload>> stream = eventStore.readStream("user", from(1))) {
		
			assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
			
			Optional<Event<EventPayload>> op = stream.findFirst();
			assertTrue(op.isPresent());
			UserCreatedEventPayload payload = (UserCreatedEventPayload) op.get().getPayload();
			assertEquals("ja", payload.getName());
			assertEquals("gmail", payload.getEmail());
			assertEquals(39, payload.getAge());	
		}
		
		assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
	}
	
	@Test
	void testEventStoreException() {
		EventStore eventStore = new DatabaseEventStore(db,new EventPayloadRegistryBuilder().build());
		EventStoreException ese = assertThrows(EventStoreException.class , () -> eventStore.appendToStream("user", from(1), new BadEventPayload()));
		assertEquals(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, ese.getType());
	}
	
	
	private static final class BadEventPayload implements EventPayload {
		
	}
}
