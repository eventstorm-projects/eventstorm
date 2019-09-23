package eu.eventstorm.sql;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.builder.DeleteBuilder;
import eu.eventstorm.sql.builder.InsertBuilder;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.builder.UpdateBuilder;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentDescriptor;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.sql.model.ex001.StudentMapper;
import eu.eventstorm.sql.tx.Transaction;
import eu.eventstorm.sql.tx.TransactionManager;
import eu.eventstorm.sql.tx.TransactionManagerImpl;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class RepositoryTest {

	public static final eu.eventstorm.sql.jdbc.Mapper<Student> STUDENT = new StudentMapper();

	private DataSource ds;
	private Database db;
	private Repository repo;

	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
		TransactionManager transactionManager = new TransactionManagerImpl(ds);
		db = new DatabaseImpl(ds, Dialect.Name.H2, transactionManager, "", new eu.eventstorm.sql.model.ex001.Module("test", null));
		repo = new Repository(db) {
		};
	}

	@AfterEach()
	void after() throws SQLException {
		ds.getConnection().createStatement().execute("SHUTDOWN");
	}

	@Test
	void testSelect() {

		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		assertEquals("SELECT id,code,age,overall_rating,created_at,readonly FROM student", builder.build());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Student s = repo.executeSelect(builder.build(), PreparedStatementSetter.EMPTY, STUDENT);
			assertNull(s);
			tx.rollback();
		}
	}

	@Test
	void testExecuteInsertSelect() throws SQLException {

		InsertBuilder insertBuilder = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS);
		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");
		student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeInsert(insertBuilder.build(), STUDENT, student);
			tx.commit();
		}

		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Student s = repo.executeSelect(builder.build(), PreparedStatementSetter.EMPTY, STUDENT);
			Assertions.assertNotNull(s);
			tx.rollback();
		}
		
		UpdateBuilder update = repo.update(StudentDescriptor.TABLE, StudentDescriptor.COLUMNS, StudentDescriptor.IDS);
		student.setAge(38);
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeUpdate(update.build(), STUDENT, student);
			tx.commit();
		}

		DeleteBuilder deleteBuilder = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeDelete(deleteBuilder.build(), ps -> ps.setInt(1, 1));
			tx.commit();
		}
	}

	@Test
	void testSelectExceptionOnPSS() {

		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class,
			        () -> repo.executeSelect(builder.build(), ps -> ps.setString(1, "fake"), STUDENT));
			assertEquals(EventstormRepositoryException.Type.SELECT_PREPARED_STATEMENT_SETTER, ex.getType());
			tx.rollback();
		}

	}

	@Test
	void testSelectExceptionOnResultSetMapper() {
		InsertBuilder insertBuilder = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS);
		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");
		student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeInsert(insertBuilder.build(), STUDENT, student);
			tx.commit();
		}

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo
			        .executeSelect(repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE).build(), PreparedStatementSetter.EMPTY, (dialect, rs) -> {
				        rs.getShort(100);
				        return null;
			        }));
			assertEquals(EventstormRepositoryException.Type.SELECT_MAPPER, ex.getType());
			tx.rollback();
		}
	}
	
	@Test
	void testSelectExceptionOnExecuteQuery() {
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class,
			        () -> repo.executeSelect("select nextval('Hello world')", PreparedStatementSetter.EMPTY, STUDENT));
			assertEquals(EventstormRepositoryException.Type.SELECT_EXECUTE_QUERY, ex.getType());
			tx.rollback();
		}
	}

	@Test
	void testStream() throws SQLException {

		String insert = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS).build();
		Student student = new StudentImpl();
		student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				student.setId(i);
				student.setAge(30 + 1);
				student.setCode("Code_" + i);
				repo.executeInsert(insert, STUDENT, student);
			}
			tx.commit();
		}

		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);

		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Stream<Student> stream = repo.stream(builder.build(), PreparedStatementSetter.EMPTY, STUDENT);
			Assertions.assertNotNull(stream);
			tx.rollback();
		}

		String delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				final int j = i;
				repo.executeDelete(delete, ps -> ps.setInt(1, j));	
			}
			tx.commit();
		}
	}

	@Test
	void testInsertException() {
		InsertBuilder insertBuilder = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS);
		Student student = new StudentImpl();
		student.setId(1);
		student.setAge(37);
		student.setCode("Code1");
		student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeInsert(insertBuilder.build(), (ps, pojo) -> {
				ps.setString(100, "hello");
			}, student));
			assertEquals(EventstormRepositoryException.Type.INSERT_MAPPER, ex.getType());
			tx.rollback();
		}

	}
	
	@Test
	void testDeleteException() {
		String delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeDelete(delete, (ps) -> {
				ps.setString(100, "hello");
			}));
			assertEquals(EventstormRepositoryException.Type.DELETE_PREPARED_STATEMENT_SETTER, ex.getType());
			
			assertEquals(0, repo.executeDelete(delete, ps -> ps.setInt(1, 1)));
			
			tx.rollback();
		}
	}
	
	
	@Test
	void testBatch() throws SQLException {

		String insert = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS).build();
		List<Student> students = new ArrayList<>();
		for (int i = 1 ; i < 101 ; i ++) {
			Student student = new StudentImpl();
			student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			student.setId(i);
			student.setAge(30 + i);
			student.setCode("Code_" + i);
			students.add(student);
		}
		
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeBatchInsert(insert, STUDENT, students);
			tx.commit();
		}

		String delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				final int j = i;
				repo.executeDelete(delete, ps -> ps.setInt(1, j));	
			}
			tx.commit();
		}
	}
	
	
}
