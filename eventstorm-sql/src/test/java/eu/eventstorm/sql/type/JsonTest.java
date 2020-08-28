package eu.eventstorm.sql.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

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
import eu.eventstorm.sql.json.JacksonJsonMapper;
import eu.eventstorm.sql.model.json.Span;
import eu.eventstorm.sql.model.json.SpanRepository;
import eu.eventstorm.sql.type.common.BlobJson;
import eu.eventstorm.sql.type.common.BlobJsonList;
import eu.eventstorm.sql.type.common.BlobJsonMap;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class JsonTest {

	private DataSource ds;
	private Database db;

	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/json.sql'", "sa", "");
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withJsonMapper(new JacksonJsonMapper())
				.withModule(new eu.eventstorm.sql.model.json.Module("test", null))
				.build();
	}

	@AfterEach()
	void after() throws SQLException {
		ds.getConnection().createStatement().execute("SHUTDOWN");
	}

	@SuppressWarnings("all")
	@Test
	void jsonMapTest() throws IOException {

		SpanRepository repo = new SpanRepository(db) {
		};

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span span = new Span();
			span.setId(1);
			span.setContent(Jsons.createMap());

			Span span2 = new Span();
			span2.setId(2);
			span2.setContent(Jsons.createMap(db.jsonMapper().readMap("{\"key1\":\"val01\"}".getBytes())));

			repo.insert(span);
			repo.insert(span2);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Span s = repo.findById(1);
			Span s2 = repo.findById(2);
			assertNull(s.getContent().asMap().get("toto", String.class));
			assertEquals("val01", s2.getContent().asMap().get("key1", String.class));
			tx.rollback();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span s2 = repo.findById(2);
			s2.getContent().asMap().put("key1", "val01__update");
			for (int i = 0; i < 100; i++) {
				s2.getContent().asMap().put("key__" + i, "val" + i);
			}
			repo.update(s2);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span s2 = repo.findById(2);
			assertEquals("val01__update", s2.getContent().asMap().get("key1", String.class));
			assertEquals("val01__update", s2.getContent().asMap().remove("key1"));
			repo.update(s2);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Span s2 = repo.findById(2);
			assertEquals(100, s2.getContent().asMap().keys().size());
			for (String key : s2.getContent().asMap().keys()) {
				assertEquals("val" + key.substring(5), s2.getContent().asMap().get(key, String.class));
			}
			tx.rollback();
		}

	}

	@SuppressWarnings("all")
	@Test
	void jsonListTest() throws IOException {

		SpanRepository repo = new SpanRepository(db) {
		};

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span span = new Span();
			span.setId(1);
			span.setContent(Jsons.createList());

			Span span2 = new Span();
			span2.setId(2);
			span2.setContent(Jsons.createList(db.jsonMapper().readList("[\"val01\",\"val02\"]".getBytes())));

			repo.insert(span);
			repo.insert(span2);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Span s = repo.findById(1);
			Span s2 = repo.findById(2);
			assertEquals(0, s.getContent().asList().size());
			assertEquals(2, s2.getContent().asList().size());
			assertEquals("val01", s2.getContent().asList().get(0, String.class));
			assertEquals("val02", s2.getContent().asList().get(1, String.class));
			tx.rollback();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span s = repo.findById(1);
			for (int i = 0; i < 100; i++) {
				s.getContent().asList().add("val" + i);
			}
			repo.update(s);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Span s = repo.findById(1);
			assertEquals(100, s.getContent().asList().size());
			for (int i = 0; i < 100; i++) {
				assertEquals("val" + i, s.getContent().asList().get(i, String.class));
			}
			tx.rollback();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			Span s = repo.findById(1);
			assertEquals("val40", s.getContent().asList().remove(40, String.class));
			assertEquals("val20", s.getContent().asList().remove(20, String.class));
			repo.update(s);
			tx.commit();
		}

	}

	@Test
	void mapListException() {

		BlobJson blobJson = new BlobJson(new JacksonJsonMapper(), new BlobJsonMap(new HashMap<>()));

		SqlTypeException ex = assertThrows(SqlTypeException.class, () -> blobJson.asList());
		assertEquals(SqlTypeException.Type.AS_LIST_INVALID, ex.getType());
		assertEquals(BlobJsonMap.class, ex.getValues().get(SqlTypeException.PARAM_ADAPTEE).getClass());

		BlobJson blobListJson = new BlobJson(new JacksonJsonMapper(), new BlobJsonList(new ArrayList<>()));
		ex = assertThrows(SqlTypeException.class, () -> blobListJson.asMap());
		assertEquals(SqlTypeException.Type.AS_MAP_INVALID, ex.getType());
		assertEquals(BlobJsonList.class, ex.getValues().get(SqlTypeException.PARAM_ADAPTEE).getClass());
	}

	private static class Pojo {
		public int getFlow() {
			throw new RuntimeException();
		}
	}
}
