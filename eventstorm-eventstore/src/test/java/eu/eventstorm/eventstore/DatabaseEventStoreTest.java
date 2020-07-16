package eu.eventstorm.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.core.Event;
import eu.eventstorm.eventstore.db.DatabaseEventStore;
import eu.eventstorm.eventstore.db.Module;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class DatabaseEventStoreTest extends EventStoreTest {

	private HikariDataSource ds;
	private Database db;

	@BeforeEach
	void init() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
		config.setUsername("sa");
		config.setPassword("");

		ds = new HikariDataSource(config);

		try (Connection conn = ds.getConnection()) {
			try (InputStream inputStream = DatabaseEventStoreTest.class.getResourceAsStream("/db/migration/eventstore/h2/V1.0.0__init-schema.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModule(new Module("event_store", ""))
				.build();
		
		super.init();
	}

	@AfterEach()
	void after() throws SQLException {
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}
	
//	@Test
//	void testAppend() {
//		
//		StreamManager manager = new InMemoryStreamManagerBuilder()
//				.withDefinition("user")
//				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
//			.and()
//			.build();
//		
//		eventStore.appendToStream(manager.getDefinition("user").getStreamEventDefinition(UserCreatedEventPayload.class.getSimpleName()), 
//				"1", UserCreatedEventPayload.newBuilder()
//					.setAge(39)
//					.setName("ja")
//					.setEmail("gmail")
//					.build());
//		
////		eventStore.appendToStream(manager.getDefinition("user").getStreamEvantPayloadDefinition(UserCreatedEventPayload.class.getSimpleName()), 
////				"1", 
////				new UserCreatedEventPayloadImpl("ja","gmail",39));
////		
//		try (Stream<Event> stream = eventStore.readStream(manager.getDefinition("user"), "1")) {
//		
//			assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
//			
//			Optional<Event> op = stream.findFirst();
//			assertTrue(op.isPresent());
//			
////			UserCreatedEventPayload.Builder builder = UserCreatedEventPayload.newBuilder();
////			try {
////				JsonFormat.parser().merge(op.get().getData().toStringUtf8(), builder);
////			} catch (InvalidProtocolBufferException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////			UserCreatedEventPayload payload = builder.build();
////			assertEquals("ja", payload.getName());
////			assertEquals("gmail", payload.getEmail());
////			assertEquals(39, payload.getAge());	
//		}
////		
////		assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
//	}
	
	@Test
	void testEventStoreException() {
		StreamManager manager = new InMemoryStreamManagerBuilder()
				.withDefinition("user")
				.and()
				.build();
		
		EventStore eventStore = new DatabaseEventStore(db, new EventStoreProperties());
		//EventStoreException ese = assertThrows(EventStoreException.class , () -> eventStore.appendToStream(manager.getDefinition("user"), "1", new BadEventPayload()));
		//assertEquals(EventStoreException.Type.FAILED_TO_SERILIAZE_PAYLOAD, ese.getType());
	}

	@Override
	protected EventStore initEventStore() {
		return new DatabaseEventStore(db, new EventStoreProperties());
	}
	
	
}
