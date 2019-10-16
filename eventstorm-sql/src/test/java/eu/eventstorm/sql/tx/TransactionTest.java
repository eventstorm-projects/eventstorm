package eu.eventstorm.sql.tx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import brave.Tracer;
import brave.Tracing;
import brave.sampler.Sampler;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.sql.tx.tracer.LoggingBraveReporter;
import eu.eventstorm.sql.tx.tracer.TransactionTracers;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class TransactionTest {

	private DataSource ds;
	private Database db;

	@BeforeEach
	void before() {
		Tracer tracer = Tracing.newBuilder().sampler(Sampler.ALWAYS_SAMPLE).spanReporter(new LoggingBraveReporter()).build().tracer();
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
		db = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.brave(tracer))), "", new eu.eventstorm.sql.model.ex001.Module("test", null));
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
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


	}
	
	@Test
	void readTest() {
		
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			
			assertEquals(true, tx.isReadOnly());
			assertThrows(EventstormTransactionException.class, () -> tx.commit());
			assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).write("XXX"));
			assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).writeAutoIncrement("XXX"));
			assertThrows(EventstormTransactionException.class, () -> ((TransactionReadOnly)tx).innerTransaction(new TransactionDefinitionReadWrite()));
			
		}
	}

	@Test
	void writeTest() {
		
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {

			assertEquals(false, tx.isReadOnly());
			
			try(TransactionNested tn = (TransactionNested) ((TransactionReadWrite)tx).innerTransaction(new TransactionDefinitionReadWrite())) {
				tn.rollback();	
			}
			
			tx.rollback();
		}
	}
}
