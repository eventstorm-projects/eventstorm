package eu.eventstorm.sql.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.dialect.Dialects;
import eu.eventstorm.sql.impl.TransactionManagerConfiguration;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.tracer.TransactionTracers;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class IdTest {

	private DataSource ds;
	private Database db;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;INIT=RUNSCRIPT FROM 'classpath:sql/sequence.sql'", "sa", "");
		//db = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new eu.eventstorm.sql.model.json.Module("test", null));
		db = Mockito.mock(Database.class);
		Module module = new Module("test") {
		};
		
		Mockito.when(db.transactionManager()).thenReturn(new TransactionManagerImpl(ds, new TransactionManagerConfiguration(TransactionTracers.debug())));
		Mockito.when(db.dialect()).thenReturn(Dialects.h2(db));
		Mockito.when(db.getModule(Mockito.<SqlSequence>any())).thenReturn(module);
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
	}

	@Test
	void simpleTest() throws IOException {
		
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			SequenceGenerator<Integer> sequenceGenerator = new SequenceGenerator4Integer(db, new SqlSequence("sequence_001"));
			assertEquals(1, sequenceGenerator.next());	
			
			SequenceGenerator<Long> sequenceGeneratorLong = new SequenceGenerator4Long(db, new SqlSequence("sequence_001"));
			assertEquals(2, sequenceGeneratorLong.next());	
			
			
			IdentifierException ie = assertThrows(IdentifierException.class, () -> new SequenceGenerator4Integer(db, new SqlSequence("sequence_002")).next());
			assertEquals(IdentifierException.Type.SEQUENCE_EXECUTE_QUERY, ie.getType());
		}
		
		
	}
}
