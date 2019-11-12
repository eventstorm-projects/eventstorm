package eu.eventstorm.core.ex001;


import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.eventstorm.core.CommandGateway;
import eu.eventstorm.core.CommandHandlerRegistry;
import eu.eventstorm.core.CommandHandlerRegistryBuilder;
import eu.eventstorm.core.EventBus;
import eu.eventstorm.core.EventStore;
import eu.eventstorm.core.eventbus.InMemoryEventBus;
import eu.eventstorm.core.eventstore.InMemoryEventStore;
import eu.eventstorm.core.ex001.command.CreateUserCommand;
import eu.eventstorm.core.ex001.gen.impl.CreateUserCommandImpl;
import eu.eventstorm.core.ex001.handler.CreateUserCommandHandler;
import eu.eventstorm.sql.Database;

class Ex001Test {

    private JdbcConnectionPool ds;
    private Database database;

    @BeforeEach
    void before() {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
      //  database = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new  io.m3.core.eventstore.IoM3CoreEventstoreModule("m3", "")
      //  ,new IoM3CoreEx001DomainModule("domain", ""));
      //  Flyway flyway = Flyway.configure().dataSource(ds).load();
      //  flyway.migrate();
    }

    @Test
    void test() {

        CreateUserCommand command = new CreateUserCommandImpl();
        command.setAge(39);
        command.setName("Jacques");
        command.setEmail("jm@mail.org");

      //  CreateUserCommand command2 = new CreateUserCommandImpl();
      //  command2.setAge(39);
      //  command2.setName("Jacques");
      //  command2.setEmail("jm@mail.org");


        EventBus eventBus = new InMemoryEventBus();
        EventStore eventStore = new InMemoryEventStore();
        
        CommandHandlerRegistry registry = new CommandHandlerRegistryBuilder()
        		.add(CreateUserCommandImpl.class, new CreateUserCommandHandler(eventStore, eventBus))
        		//.add(CreateUserCommandImpl.class, new CreateUserCommandHandler(eventStore, eventBus))
        		.build();

         
      //  eventBus.register(new UserDomainHandler(database));
      //  EventStore eventStore = new H2EventStore(database);
         CommandGateway gateway = new CommandGateway(registry, eventBus);



        gateway.dispatch(command);
       // gateway.dispatch(command2);

      //  CreateUserCommandHandlerGateway gateway = new CreateUserCommandHandlerGateway(new CreateUserCommandHandler(), eventBus);
      //  gateway.dispatch(command);

       // commandGateway.dispatch(command);

       // EventStore store = new H2EventStore(database);

       // store.store(new CreatedUserCommandEvent(command));

    }
}
