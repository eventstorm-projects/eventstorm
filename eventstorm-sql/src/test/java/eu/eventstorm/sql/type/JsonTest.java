package eu.eventstorm.sql.type;

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
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(LoggerInstancePostProcessor.class)
class JsonTest {

    private JdbcConnectionPool ds;
    private Database db;

    @BeforeEach
    void before() {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test_json;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/json.sql'", "sa", "");
        db = DatabaseBuilder.from(Dialect.Name.H2)
                .withTransactionManager(new TransactionManagerImpl(ds))
                .withJsonMapper(new JacksonJsonMapper())
                .withModule(new eu.eventstorm.sql.model.json.Module("test", null))
                .build();
    }

    @AfterEach()
    void after() throws SQLException {
        db.close();
        try (Connection c = ds.getConnection()) {
            try (Statement st = c.createStatement()) {
                st.execute("SHUTDOWN");
            }
        }
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
            tx.commit();
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
            tx.commit();
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
            span2.setContent(Jsons.createList(db.jsonMapper().readList("[\"val01\",\"val02\"]".getBytes(), String.class)));

            repo.insert(span);
            repo.insert(span2);
            tx.commit();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            Span s = repo.findById(1);
            Span s2 = repo.findById(2);
            assertEquals(0, s.getContent().asList(String.class).size());
            assertEquals(2, s2.getContent().asList(String.class).size());
            assertEquals("val01", s2.getContent().asList(String.class).get(0));
            assertEquals("val02", s2.getContent().asList(String.class).get(1));
            tx.rollback();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
            Span s = repo.findById(1);
            for (int i = 0; i < 100; i++) {
                s.getContent().asList(String.class).add("val" + i);
            }
            repo.update(s);
            tx.commit();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            Span s = repo.findById(1);
            assertEquals(100, s.getContent().asList(String.class).size());
            for (int i = 0; i < 100; i++) {
                assertEquals("val" + i, s.getContent().asList(String.class).get(i));
            }
            tx.rollback();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
            Span s = repo.findById(1);
            assertEquals("val40", s.getContent().asList(String.class).remove(40));
            assertEquals("val20", s.getContent().asList(String.class).remove(20));
            repo.update(s);
            tx.commit();
        }

    }

    @Test
    void mapListException() {

        BlobJson blobJson = new BlobJson(new JacksonJsonMapper(), new BlobJsonMap(new HashMap<>()));

        SqlTypeException ex = assertThrows(SqlTypeException.class, () -> blobJson.asList(String.class));
        assertEquals(SqlTypeException.Type.AS_LIST_INVALID, ex.getType());
        assertEquals(BlobJsonMap.class, ex.getValues().get(SqlTypeException.PARAM_ADAPTEE).getClass());

        BlobJson blobListJson = new BlobJson(new JacksonJsonMapper(), new BlobJsonList(new ArrayList<>()));
        ex = assertThrows(SqlTypeException.class, blobListJson::asMap);
        assertEquals(SqlTypeException.Type.AS_MAP_INVALID, ex.getType());
        assertEquals(BlobJsonList.class, ex.getValues().get(SqlTypeException.PARAM_ADAPTEE).getClass());
    }

    private static class Pojo {
        public int getFlow() {
            throw new RuntimeException();
        }
    }
}
