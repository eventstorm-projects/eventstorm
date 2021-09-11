package eu.eventstorm.cqrs.impl;


import eu.eventstorm.cqrs.EventLoop;
import eu.eventstorm.cqrs.event.EvolutionHandlers;
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
import eu.eventstorm.sql.impl.TransactionManagerConfiguration;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.TransactionTracers;
import eu.eventstorm.sql.util.TransactionTemplate;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

import javax.sql.DataSource;

@Configuration
@ComponentScan
class ReactiveLocalDatabaseEventStoreCommandHandlerConfiguration {

    @Bean
    LocalDatabaseEventStore localDatabaseEventStore(Database database) {
        return new LocalDatabaseEventStore(database, new EventStoreProperties(),null);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new TransactionManagerImpl(dataSource, new TransactionManagerConfiguration(TransactionTracers.noOp()));
    }

    @Bean
    TransactionTemplate transactionTemplate(TransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
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
        return JdbcConnectionPool.create("jdbc:h2:mem:cqrs-reactive-test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/V1.0.0__init-schema.sql'", "sa", "");
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

    @Bean
    EvolutionHandlers evolutionHandlers() {
        return EvolutionHandlers.newBuilder()
                .build();
    }
}
