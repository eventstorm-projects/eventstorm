package eu.eventstorm.sql;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.jdbc.PreparedStatementSetters.noParameter;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.builder.DeleteBuilder;
import eu.eventstorm.sql.builder.InsertBuilder;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.builder.SqlQueryImpl;
import eu.eventstorm.sql.builder.UpdateBuilder;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.impl.DatabaseBuilder;
import eu.eventstorm.sql.impl.TransactionManagerImpl;
import eu.eventstorm.sql.jdbc.Batch;
import eu.eventstorm.sql.jdbc.ResultSetMappers;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentDescriptor;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.sql.model.ex001.StudentMapper;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class RepositoryTest {

	public static final eu.eventstorm.sql.jdbc.Mapper<Student> STUDENT = new StudentMapper();

	private JdbcConnectionPool ds;
	private Database db;
	private Repository repo;

	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test2;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
		TransactionManager transactionManager = new TransactionManagerImpl(ds);
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(transactionManager)
				.withModule(new eu.eventstorm.sql.model.ex001.Module("test", null))
				.build();
		repo = new Repository(db) {
		};
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

	@Test
	void testSelect() {

		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		assertEquals("SELECT id,code,age,overall_rating,created_at,readonly FROM student", builder.<SqlQuery>build().sql());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Student s = repo.executeSelect(builder.build(), noParameter(), STUDENT);
			assertNull(s);
			tx.rollback();
		}
	}

	@Test
	void testExecuteInsertSelect() {

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
			Student s = repo.executeSelect(builder.build(), noParameter(), STUDENT);
			assertNotNull(s);
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
			        .executeSelect(repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE).build(), noParameter(), (dialect, rs) -> {
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
			        () -> repo.executeSelect(new SqlQueryImpl("select nextval('Hello world')"), noParameter(), STUDENT));
			assertEquals(EventstormRepositoryException.Type.SELECT_EXECUTE_QUERY, ex.getType());
			tx.rollback();
		}
	}

	@Test
	void testStream() {

		SqlQuery insert = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS).build();
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
			try (Stream<Student> stream = repo.stream(builder.build(), noParameter(), STUDENT)) {
				assertNotNull(stream);
				
				List<Student> list = stream.collect(toList());
				assertEquals(100, list.size());	
			}
			tx.rollback();
		}
		
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
		    EventstormRepositoryException ere = assertThrows(EventstormRepositoryException.class, () -> repo.stream(builder.build(), ps -> ps.setString(1, "failed"), STUDENT));
		    assertEquals(EventstormRepositoryException.Type.STREAM_PREPARED_STATEMENT_SETTER, ere.getType());
		    tx.rollback();
		}

		SqlQuery delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				final int j = i;
				repo.executeDelete(delete, ps -> ps.setInt(1, j));
			}
			tx.commit();
		}
		
		assertEquals(0, ds.getActiveConnections());
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
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeInsert(insertBuilder.build(), (dial, ps, pojo) -> ps.setString(100, "hello"), student));
			assertEquals(EventstormRepositoryException.Type.INSERT_MAPPER, ex.getType());
			tx.rollback();
		}

	}

	@Test
	void testDeleteException() {
		SqlQuery delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeDelete(delete, (ps) -> ps.setString(100, "hello")));
			assertEquals(EventstormRepositoryException.Type.DELETE_PREPARED_STATEMENT_SETTER, ex.getType());

			assertEquals(0, repo.executeDelete(delete, ps -> ps.setInt(1, 1)));

			tx.rollback();
		}
	}


	@Test
	void testBatch() {

		SqlQuery insert = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS).build();

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
            try (Batch<Student> batch = repo.batch(insert, STUDENT)) {
                for (int i = 1 ; i < 101 ; i ++) {
                    Student student = new StudentImpl();
			        student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			        student.setId(i);
			        student.setAge(30 + i);
			        student.setCode("Code_" + i);
                    batch.add(student);
                }
            }
			tx.commit();
        }

		SqlQuery select  = repo.select(AggregateFunctions.count(StudentDescriptor.ID))
                .from(StudentDescriptor.TABLE)
                .build();

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            long count = repo.executeSelect(select, noParameter(), ResultSetMappers.LONG);
            assertEquals(100, count);
			tx.rollback();
        }

        SqlQuery delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				final int j = i;
				repo.executeDelete(delete, ps -> ps.setInt(1, j));
			}
			tx.commit();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            long count = repo.executeSelect(select, noParameter(), ResultSetMappers.LONG);
            assertEquals(0, count);
			tx.rollback();
        }
	}
	
	
	@Test
	void testBatchWithIdentifierGenerator() {

		SqlQuery insert = repo.insert(StudentDescriptor.TABLE, StudentDescriptor.IDS, StudentDescriptor.COLUMNS).build();

		AtomicInteger atomicInteger = new AtomicInteger(1);

		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
            try (Batch<Student> batch = repo.batch(insert, STUDENT, atomicInteger::getAndIncrement, Student::setId)) {
                for (int i = 1 ; i < 101 ; i ++) {
                    Student student = new StudentImpl();
			        student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			        student.setAge(30 + i);
			        student.setCode("Code_" + i);
                    batch.add(student);
                }
            }
			tx.commit();
        }

		
		SqlQuery select  = repo.select(AggregateFunctions.count())
                .from(StudentDescriptor.TABLE)
                .build();

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            long count = repo.executeSelect(select, noParameter(), ResultSetMappers.LONG);
            assertEquals(100, count);
			tx.rollback();
        }

        SqlQuery delete = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID)).build();
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			for (int i = 1 ; i < 101 ; i ++) {
				final int j = i;
				repo.executeDelete(delete, ps -> ps.setInt(1, j));
			}
			tx.commit();
        }

        try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
            long count = repo.executeSelect(select, noParameter(), ResultSetMappers.LONG);
            assertEquals(0, count);
			tx.rollback();
        }
	}


}
