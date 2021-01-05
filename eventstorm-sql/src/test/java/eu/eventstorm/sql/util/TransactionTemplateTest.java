package eu.eventstorm.sql.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.impl.TransactionDefinitions;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import reactor.core.publisher.Flux;

@ExtendWith(LoggerInstancePostProcessor.class)
class TransactionTemplateTest {
	
	private TransactionTemplate template;
	private JdbcConnectionPool ds;
	private AbstractStudentRepository repository;
	private Database db;

	@BeforeEach
	void before() throws SQLException, IOException {

		ds = JdbcConnectionPool.create("jdbc:h2:mem:test_tx;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", "sa", "");

		try (Connection conn = ds.getConnection()) {
			try (InputStream inputStream = TransactionTemplateTest.class.getResourceAsStream("/sql/ex001.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		}

		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModule(new eu.eventstorm.sql.model.ex001.Module("test", null))
				.build();

		repository = new AbstractStudentRepository(db) {
        };

        template = new TransactionTemplate(db.transactionManager());
				
	}

	@AfterEach()
	void after() throws SQLException{
		try (Connection c = ds.getConnection()) {
			try (Statement st = c.createStatement()) {
				st.execute("SHUTDOWN");
			}
		}
		db.close();
		ds.dispose();
	}

	@Test
	void simpleTest() {

		Student student = new StudentImpl();
        student.setId(1);
        student.setAge(37);
        student.setCode("Code1");
      
       
        template.executeWithReadWrite(() -> this.repository.insert(student));
        
        Student fresh = template.executeWithReadOnly(() -> repository.findById(1));
        assertNotNull(fresh);

		template.executeWithReadOnly(() -> {
			Student inside = template.executeWithReadOnly(() -> repository.findById(1));
			assertNotNull(inside);
		});

		template.executeWith(
				TransactionDefinitions.readOnly(5),
				() -> {
					Student inside = template.executeWithReadOnly(() -> repository.findById(1));
					assertNotNull(inside);
			});

	}

	@Test
	void streamTest() {

		Student student = new StudentImpl();
		student.setId(2);
		student.setAge(37);
		student.setCode("Code2");


		template.executeWithReadWrite(() -> this.repository.insert(student));

		try(Stream<Student> students = template.stream(() -> repository.findAll())) {
			assertEquals(1, students.count());
		}

		Assertions.assertThrows(RuntimeException.class, () -> template.stream(() -> {
			throw new RuntimeException();
		}));

	}

	@Test
	void FluxTest() {

		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");

		Student s2 = new StudentImpl();
		s2.setId(2);
		s2.setAge(37);
		s2.setCode("Code2");

		template.executeWithReadWrite(() -> {
			this.repository.insert(student);
			this.repository.insert(s2);
		});

		Flux<Student> students = template.flux(() -> repository.findAll());
		List<Integer> collect = students.map(Student::getId).collectList().block();

		assertEquals(1, collect.get(0));
		assertEquals(2, collect.get(1));

	}
	
	@Test
	void testRollback() {

		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");

		Assertions.assertThrows(RuntimeException.class, () -> template.executeWithReadWrite(() -> {
					repository.insert(student);
					throw new RuntimeException();
				}));

		assertNull(template.executeWithReadOnly(() -> repository.findById(1))); 

		Assertions.assertThrows(RuntimeException.class, () -> template.executeWithReadOnly(() -> {
			throw new RuntimeException();
		}));

		Assertions.assertThrows(RuntimeException.class, () -> template.executeWithReadWrite(new TransactionCallbackVoid() {
			@Override
			public void doInTransaction() {
				throw new RuntimeException();
			}
		}));

	}
	

	@Test
	void simpleIsolated() {

		Student student = new StudentImpl();
        student.setId(1);
        student.setAge(37);
        student.setCode("Code1");

        Student s2 = new StudentImpl();
        s2.setId(2);
        s2.setAge(38);
        s2.setCode("Code2");
       
        Assertions.assertThrows(RuntimeException.class, () -> template.executeWithIsolatedReadWrite(() -> {
        	this.repository.insert(student);
        	assertEquals(1, template.executeWithReadOnly(() -> this.repository.findAll().count()));
        	template.executeWithIsolatedReadWrite(() -> {
            	this.repository.insert(s2);
            	return null;
            });
        	assertEquals(2, template.executeWithReadOnly(() -> this.repository.findAll().count()));
        	throw new RuntimeException();
        }));

        assertEquals(1, template.executeWithReadOnly(() -> this.repository.findAll().count()));
        
        assertNull(template.executeWithReadOnly(() -> repository.findById(1)));
        assertNotNull(template.executeWithReadOnly(() -> repository.findById(2)));

	}

	@Test
	void timeoutTest() {

		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");

		Exception ex = template.executeWith(TransactionDefinitions.readWrite(1), () -> {
			this.repository.insert(student);
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				return e;
			}
			return null;
		});
		assertNotNull(ex);
	}
}
