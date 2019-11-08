package eu.eventstorm.sql.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.impl.Transaction;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.xml.Span;
import eu.eventstorm.sql.model.xml.SpanRepository;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.util.FastByteArrayInputStream;
import eu.eventstorm.util.Streams;

@ExtendWith(LoggerInstancePostProcessor.class)
class XmlTest {

	private DataSource ds;
	private Database db;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/xml.sql'", "sa", "");
		db = new DatabaseImpl(Dialect.Name.H2, new TransactionManagerImpl(ds), "", new eu.eventstorm.sql.model.xml.Module("test", null));
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
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
