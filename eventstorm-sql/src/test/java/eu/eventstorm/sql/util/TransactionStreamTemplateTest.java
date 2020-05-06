package eu.eventstorm.sql.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionException;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;

class TransactionStreamTemplateTest {
	
	private TransactionTemplate template;
	private TransactionStreamTemplate streamTemplate;
	private HikariDataSource ds;
	private AbstractStudentRepository repository;

	@BeforeEach
	void before() throws SQLException, IOException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
		config.setUsername("sa");
		config.setPassword("");

		ds = new HikariDataSource(config);

		try (Connection conn = ds.getConnection()) {
			try (InputStream inputStream = TransactionStreamTemplateTest.class.getResourceAsStream("/sql/ex001.sql")) {
				RunScript.execute(conn, new InputStreamReader(inputStream));
			}
		}

		Database db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(new TransactionManagerImpl(ds))
				.withModule(new eu.eventstorm.sql.model.ex001.Module("test", null))
				.build();
		
		template = new TransactionTemplate(db.transactionManager());
		streamTemplate = new TransactionStreamTemplate(db);
		
		repository = new AbstractStudentRepository(db) {
        };
				
	}

	@AfterEach()
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
		ds.close();
	}

	@Test
	void simpleTest() {

		assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
		template.executeWithReadWrite(() -> {
        	assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
        	Student student = new StudentImpl();
            student.setId(1);
            student.setAge(37);
            student.setCode("Code1");
            repository.insert(student);
        	return student;
        });
        
        assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
        try (Stream<Student> stream = streamTemplate.decorate(() -> repository.findAll())) {
        	assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
        }
        assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());

        
        try (Stream<Student> stream = streamTemplate.decorate(() -> repository.findAll())) {
        	assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
        	try (Stream<Student> stream2 = streamTemplate.decorate(() -> repository.findAll())) {
            	assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
            }
        }
	}
	
	@Test
	void testInvalid() {

		assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
        template.executeWithReadWrite(() -> {
        	assertEquals(1, ds.getHikariPoolMXBean().getActiveConnections());
        	assertThrows(TransactionException.class, () -> streamTemplate.decorate(() -> repository.findAll()));
        	return null;
        });
        
        assertEquals(0, ds.getHikariPoolMXBean().getActiveConnections());
	}
	
	
}
