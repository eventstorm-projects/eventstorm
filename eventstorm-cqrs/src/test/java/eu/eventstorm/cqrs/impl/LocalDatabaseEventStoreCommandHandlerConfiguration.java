package eu.eventstorm.cqrs.impl;


import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
import eu.eventstorm.cqrs.impl.LocalDatabaseEventStoreCommandHandlerTest.TestLocalDatabaseEventStoreCommandHandler;
import eu.eventstorm.cqrs.tracer.NoOpTracer;
import eu.eventstorm.cqrs.tracer.Tracer;
import eu.eventstorm.eventbus.EventBus;
import eu.eventstorm.eventbus.NoEventBus;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.db.LocalDatabaseEventStore;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;
import zipkin2.reporter.brave.ZipkinSpanHandler;

import javax.sql.DataSource;

@Configuration
class LocalDatabaseEventStoreCommandHandlerConfiguration {

    @Bean
    EventStoreProperties eventStoreProperties() {
        EventStoreProperties eventStoreProperties = new EventStoreProperties();
        return new EventStoreProperties();
    }

    @Bean
    TestLocalDatabaseEventStoreCommandHandler TestLocalDatabaseEventStoreCommandHandler() {
        return new TestLocalDatabaseEventStoreCommandHandler();
    }

    @Bean
    LocalDatabaseEventStore localDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties) {
        return new LocalDatabaseEventStore(database, eventStoreProperties,null);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new TransactionManagerImpl(dataSource);
    }

    @Bean
    Database database(TransactionManager transactionManager) {
        return DatabaseBuilder.from(Dialect.Name.H2)
                .withTransactionManager(transactionManager)
                .withModule(new eu.eventstorm.eventstore.db.Module("test", null))
                .build();
    }

    @Bean
    DataSource dataSource() {
        return JdbcConnectionPool.create("jdbc:h2:mem:cqrs-local-test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/V1.0.0__init-schema.sql'", "sa", "");
    }

    @Bean
    EvolutionHandlers evolutionHandlers() {
        return EvolutionHandlers.newBuilder()
                .build();
    }

    @Bean
    EventBus eventBus() {
        return new NoEventBus();
    }

    @Bean
    EventLoop eventLoop() {
        return EventLoops.single(Schedulers.newSingle("event-loop-junit"), Schedulers.newSingle("event-post-junit"));
    }

    @Bean
    Tracer tracer() {
        return NoOpTracer.INSTANCE;
    }
}
