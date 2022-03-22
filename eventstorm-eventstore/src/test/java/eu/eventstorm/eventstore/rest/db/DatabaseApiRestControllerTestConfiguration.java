package eu.eventstorm.eventstore.rest.db;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.TypeRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.eventstorm.core.json.CoreApiModule;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.eventstore.db.DatabaseEventStore;
import eu.eventstorm.eventstore.ex.UserCreatedEventPayload;
import eu.eventstorm.eventstore.memory.InMemoryStreamManagerBuilder;
import eu.eventstorm.eventstore.rest.ApiRestReadController;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

@Profile("db")
@Configuration
@ComponentScan(basePackageClasses = ApiRestReadController.class)
@EnableWebFlux
@EnableAutoConfiguration
public class DatabaseApiRestControllerTestConfiguration implements WebFluxConfigurer {

	
	@Autowired
	ObjectMapper objectMapper;

    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(objectMapper)
        );

        configurer.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(objectMapper)
        );
    }
	
	@Bean
	EventStore eventStore(StreamManager streamManager) throws SQLException, IOException {

			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:h2:mem:test_event_store;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
			config.setUsername("sa");
			config.setPassword("");

		HikariDataSource ds = new HikariDataSource(config);

			try (Connection conn = ds.getConnection()) {
				try (InputStream inputStream = DatabaseApiRestControllerTestConfiguration.class.getResourceAsStream("/db/migration/eventstore/h2/V1.0.0__init-schema.sql")) {
					RunScript.execute(conn, new InputStreamReader(inputStream));
				}
			}

		Database db = DatabaseBuilder.from(Dialect.Name.H2)
					.withTransactionManager(new TransactionManagerImpl(ds))
					.withModule(new eu.eventstorm.eventstore.db.Module("event_store", ""))
					.build();

		return new DatabaseEventStore(db, new EventStoreProperties(), streamManager);
	}
	
	@Bean
	StreamManager streamManager() {
		return new InMemoryStreamManagerBuilder()
				.withDefinition("user")
				.withPayload(UserCreatedEventPayload.class, UserCreatedEventPayload.getDescriptor(), UserCreatedEventPayload.parser(), () -> UserCreatedEventPayload.newBuilder())
			.and()
			.build();
	}
	
	@Bean
	Scheduler scheduler() {
		return Schedulers.newSingle("event-loop");
	}
	
	@Bean
	TypeRegistry typeRegistry() {
		return TypeRegistry.newBuilder()
			.add(UserCreatedEventPayload.getDescriptor())
			.build();
	}
	
	@Bean
	Module coreApiModule(TypeRegistry typeRegistry) {
		return new CoreApiModule(typeRegistry);
	}
	
}
