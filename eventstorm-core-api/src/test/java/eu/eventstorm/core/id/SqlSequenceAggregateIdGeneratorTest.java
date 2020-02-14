package eu.eventstorm.core.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.id.SequenceGenerator4Integer;
import eu.eventstorm.sql.id.SequenceGenerator4Long;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;

class SqlSequenceAggregateIdGeneratorTest {

	private HikariDataSource ds;
	private Database db;
	private SqlSequence sequence = new SqlSequence("sequence_001");
	
	@BeforeEach
	void before() throws SQLException, IOException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
		config.setUsername("sa");
		config.setPassword("");

		ds = new HikariDataSource(config);

		try (Connection conn = ds.getConnection()) {
			try (InputStream inputStream = SqlSequenceAggregateIdGeneratorTest.class.getResourceAsStream("/sql/sequence.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		}
		
		Module module = new Module("fake") {
		};
		
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModuleAndExternalConfig(module).withSequence(sequence)
				.and().build();
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}
	
	@Test
	void testInteger() {
		AggregateIdGenerator generator = AggregateIdGeneratorFactory.sequenceInteger(new SequenceGenerator4Integer(db, sequence));
		for (int i = 1 ; i < 20 ; i++) {
			assertEquals(AggregateIds.from(i),  generator.generate());	
		}
	}

	@Test
	void testLong() {
		AggregateIdGenerator generator = AggregateIdGeneratorFactory.sequenceLong(new SequenceGenerator4Long(db, sequence));
		for (long i = 1 ; i < 20 ; i++) {
			assertEquals(AggregateIds.from(i),  generator.generate());	
		}
	}

}
