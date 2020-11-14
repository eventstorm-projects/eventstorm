package eu.eventstorm.sql.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Transaction;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.xml.Span;
import eu.eventstorm.sql.model.xml.SpanRepository;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Streams;

@ExtendWith(LoggerInstancePostProcessor.class)
class XmlTest {

	private JdbcConnectionPool ds;
	private Database db;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test_xml;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/xml.sql'", "sa", "");
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModule(new eu.eventstorm.sql.model.xml.Module("test", null))
				.build();
	}

	@AfterEach()
	void after() throws SQLException{
		db.close();
		try (Connection c = ds.getConnection()) {
			try (Statement st = c.createStatement()) {
				st.execute("SHUTDOWN");
			}
		}
		ds.dispose();
	}

	@SuppressWarnings("all")
	@Test
	void simpleTest() throws IOException {
		
		SpanRepository repo = new SpanRepository(db) {
		};

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
        	Span span = new Span();
        	span.setContent(db.dialect().createXml(new FastByteArrayInputStream("<hello>world!!</hello>".getBytes())));
        	
        	Span span2 = new Span();
        	span2.setContent(db.dialect().createXml(new FastByteArrayInputStream("<hello>world!! 2</hello>".getBytes())));
        	
        	repo.insert(span);
        	repo.insert(span2);
        	tx.commit();
        }
		
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Span s = repo.findById(1);
			Span s2 = repo.findById(2);
			
			try (InputStream is = s.getContent().getBinaryStream()) {
				assertEquals("<hello>world!!</hello>", new String(Streams.copyToByteArray(is)));
			}
			try (InputStream is = s2.getContent().getBinaryStream()) {
				assertEquals("<hello>world!! 2</hello>", new String(Streams.copyToByteArray(is)));
			}			
			tx.rollback();
		}
				
	}
}
