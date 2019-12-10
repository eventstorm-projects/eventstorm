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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.ex001.event.UserCreatedEventPayload;
import eu.eventstorm.core.ex001.gen.event.UserCreatedEventImpl;
import eu.eventstorm.core.impl.EventPayloadSchemaRegistryBuilder;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.jackson.CommandDeserializerException;
import eu.eventstorm.core.json.jackson.ParserConsumer;
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
		
		EventPayloadSchemaRegistryBuilder builder = new EventPayloadSchemaRegistryBuilder();
		builder.add(UserCreatedEventPayload.class, null, new UserCreatedEventPayloadDeserializer());
		
		EventStore eventStore = new DatabaseEventStore(db, builder.build());
		
		
		eventStore.appendToStream("user", from(1), new UserCreatedEventImpl("ja","gmail",39));
		eventStore.appendToStream("user", from(1), new UserCreatedEventImpl("ja","gmail",39));
		
		
		eventStore.readStream("user", from(1)).forEach(System.out::println);
		
	}
	
	
	private static final class UserCreatedEventPayloadBuilder {
	    
	    private String name;
	    private String email;
	    private int age;

        public UserCreatedEventPayloadBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserCreatedEventPayloadBuilder email(String email) {
            this.email=email;
            return this;
        }

        public UserCreatedEventPayloadBuilder age(int age) {
            this.age = age;
            return this;
        }
        
        UserCreatedEventPayload build() {
            return new UserCreatedEventImpl(name, email, age);
        }
	    
	}

	private static final class UserCreatedEventPayloadStdDeserializer extends StdDeserializer<UserCreatedEventPayload> {

	    private static final ImmutableMap<String, ParserConsumer<UserCreatedEventPayloadBuilder>> FIELDS;
	    static {
	        FIELDS = ImmutableMap.<String, ParserConsumer<UserCreatedEventPayloadBuilder>>builder()
	                .put("name", (parser, builder) -> builder.name(parser.nextTextValue()))
	                .put("email", (parser, builder) -> builder.email(parser.nextTextValue()))
	                .put("age", (parser, builder) -> builder.age(parser.nextIntValue(0)))
	                .build();
	    }

	    UserCreatedEventPayloadStdDeserializer() {
	        super(UserCreatedEventPayload.class);
	    }

	    @Override
	    public UserCreatedEventPayload deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
	        if (JsonToken.START_OBJECT != p.currentToken()) {
	            throw new CommandDeserializerException(CommandDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("expected",JsonToken.START_OBJECT,"current", p.currentToken()));
	        }
	        UserCreatedEventPayloadBuilder builder = new UserCreatedEventPayloadBuilder();
	        p.nextToken();
	        while (p.currentToken() != JsonToken.END_OBJECT) {
	            if (JsonToken.FIELD_NAME != p.currentToken()) {
	                throw new CommandDeserializerException(CommandDeserializerException.Type.PARSE_ERROR, ImmutableMap.of("expected",JsonToken.FIELD_NAME,"current", p.currentToken()));
	            }
	            ParserConsumer<UserCreatedEventPayloadBuilder> consumer = FIELDS.get(p.currentName());
	            if (consumer == null) {
	                throw new CommandDeserializerException(CommandDeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of("field",p.currentName(),"command", "CreateMissionObjectCodeCommand"));
	            }
	            consumer.accept(p, builder);
	            p.nextToken();
	        }
	        return builder.build();
	     }
	}
	
	
	private static final class UserCreatedEventPayloadDeserializer  implements Deserializer<UserCreatedEventPayload> {
        @Override
        public UserCreatedEventPayload deserialize(byte[] bytes) {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module. addDeserializer(UserCreatedEventPayload.class, new UserCreatedEventPayloadStdDeserializer());
            mapper.registerModule(module);
            try {
                return mapper.readValue(bytes, UserCreatedEventPayload.class);
            } catch (IOException e) {               
                e.printStackTrace();
                return null;
            }
        }
	}
}
