package eu.eventstorm.sql.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.sql.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tracer.TransactionTracers;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class TransactionTest {

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
			try (InputStream inputStream = TransactionTest.class.getResourceAsStream("/sql/ex001.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		}

		Tracer tracer = Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).spanReporter(new LoggingBraveReporter()).build().tracer();
		db = new DatabaseImpl(Dialect.Name.H2, new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.brave(tracer))), "",
		        new eu.eventstorm.sql.model.ex001.Module("test", null));
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}

	@SuppressWarnings("all")
	@Test
	void simpleTest() {

		AbstractStudentRepository repository = new AbstractStudentRepository(db) {
        };

        UUID uuid;

        try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
        	uuid = tx.getUuid();
        	Student student = new StudentImpl();
            student.setId(1);
            student.setAge(37);
            student.setCode("Code1");
            repository.insert(student);
            tx.commit();
        }
        
        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
        	assertNotNull(repository.findById(1));
        	tx.rollback();
        }


	}
	
	@Test
	void testIsolatedReadWrite() {

		AbstractStudentRepository repository = new AbstractStudentRepository(db) {
        };

        try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
        	Student student = new StudentImpl();
            student.setId(2);
            student.setAge(37);
            student.setCode("Code1");
            repository.insert(student);
            
            try (Transaction isolated = db.transactionManager().newTransactionIsolatedReadWrite()) {
            	assertNull(repository.findById(2));
            	Student s2 = new StudentImpl();
            	s2.setId(3);
            	s2.setAge(37);
            	s2.setCode("Code1");
                repository.insert(s2);
                isolated.commit();
            }
            tx.commit();
        }
        
        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
        	assertNotNull(repository.findById(2));
        	assertNotNull(repository.findById(3));
        	tx.rollback();
        }

	}
	
	@Test
	void readTest() {
		
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			
			assertEquals(true, tx.isReadOnly());
			assertThrows(EventstormTransactionException.class, () -> tx.commit());
			assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).write("XXX"));
			assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).writeAutoIncrement("XXX"));
			//assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).innerTransaction(new TransactionDefinitionReadWrite()));
			
		}
	}

	@Test
	void writeTest() {
		
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {

			assertEquals(false, tx.isReadOnly());
			
			try(TransactionNested tn = (TransactionNested) ((TransactionReadWrite)tx).innerTransaction(TransactionDefinition.READ_WRITE)) {
				tn.rollback();	
			}
			
			tx.rollback();
		}
	}
}
