package eu.eventstorm.sql.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.tx.TransactionManagerImpl;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class IdTest {

	private DataSource ds;
	private Database db;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/sequence.sql'", "sa", "");
		db = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new eu.eventstorm.sql.model.json.Module("test", null));
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
	}

	@Test
	void simpleTest() throws IOException {
		
		//SequenceGenerator<Integer> sequenceGenerator = new SequenceGenerator4Integer(db, new SqlSequence("sequence_001"));
		//assertEquals(1, sequenceGenerator.next());
		
	}
}
