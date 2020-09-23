package eu.eventstorm.sql.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class TransactionTemplateTest {
	
	private TransactionTemplate template;
	private HikariDataSource ds;
	private AbstractStudentRepository repository;
	private Database db;

	@BeforeEach
	void before() throws SQLException, IOException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE");
		config.setUsername("sa");
		config.setPassword("");

		ds = new HikariDataSource(config);

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
		db.close();
		try (java.sql.Statement st = ds.getConnection().createStatement()) {
			st.execute("SHUTDOWN");
		}
		ds.close();
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

	}
	
	@Test
	void testRollback() {

		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");

		Assertions.assertThrows(RuntimeException.class,
				() -> template.executeWithReadWrite(() -> {
					repository.insert(student);
					throw new RuntimeException();
				}));

		assertNull(template.executeWithReadOnly(() -> repository.findById(1))); 

		Assertions.assertThrows(RuntimeException.class, () -> template.executeWithReadOnly(() -> {
			throw new RuntimeException();
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
}
