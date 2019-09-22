package eu.eventstorm.sql;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.builder.DeleteBuilder;
import eu.eventstorm.sql.builder.InsertBuilder;
import eu.eventstorm.sql.builder.SelectBuilder;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentDescriptor;
import eu.eventstorm.sql.model.ex001.StudentImpl;
import eu.eventstorm.sql.model.ex001.StudentMapper;
import eu.eventstorm.sql.tx.Transaction;
import eu.eventstorm.sql.tx.TransactionManagerImpl;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class RepositoryTest {

	public static final eu.eventstorm.sql.jdbc.Mapper<Student> STUDENT = new StudentMapper();

	private DataSource ds;
	private Database db;
	
	@BeforeEach
	void before() {
		ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
		db = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new eu.eventstorm.sql.model.ex001.Module("test", null));
	}
	
	@AfterEach() 
	void after() throws SQLException{
		ds.getConnection().createStatement().execute("SHUTDOWN");
	}

	@Test
	void testSelect() {
		Repository repo = new Repository(db) {
		};
		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		assertEquals("SELECT id,code,age,overall_rating,created_at,readonly FROM student", builder.build());
	}
	
	@Test
	void testExecuteInsertSelect() throws SQLException {

		Repository repo = new Repository(db) {
		};
		
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
		
		DeleteBuilder deleteBuilder = repo.delete(StudentDescriptor.TABLE).where(eq(StudentDescriptor.ID));
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeDelete(deleteBuilder.build(), ps -> ps.setInt(1, 1));
			tx.commit();
		}
	}
	
	@Test
	void testSelectException() {

		Repository repo = new Repository(db) {
		};
		
		SelectBuilder builder = repo.select(StudentDescriptor.ALL).from(StudentDescriptor.TABLE);
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeSelect(builder.build(), ps -> ps.setString(1, "fake"), STUDENT));
			assertEquals(EventstormRepositoryException.Type.PREPARED_STATEMENT_SETTER, ex.getType());
			tx.rollback();
		}
		
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
			EventstormRepositoryException ex = assertThrows(EventstormRepositoryException.class, () -> repo.executeSelect(builder.build(), PreparedStatementSetter.EMPTY, (dialect, rs) -> {
				rs.getShort(100);
				return null;
			}));
			assertEquals(EventstormRepositoryException.Type.RESULT_SET_MAPPER, ex.getType());
			tx.rollback();
		}
	}
	
	/*
	

	@Test
	void testExecuteSelectFailsOnPrepareStatement() throws SQLException {
		when(conn.prepareStatement(anyString(),  anyInt())).thenThrow(new SQLException());

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			EventstormTransactionException ex = assertThrows(EventstormTransactionException.class, () -> repo.executeSelect(builder.build(), pss, map));
			assertEquals(EventstormTransactionException.Type.PREPARED_STATEMENT, ex.getType());
			tx.rollback();
		}
		verify(conn).close();
	}

	@Test
	void testExecuteSelectFailsOnFillPrepareStatement() throws SQLException {
		doThrow(SQLException.class).when(pss).set(Mockito.any());
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			M3RepositoryException ex = assertThrows(M3RepositoryException.class, () -> repo.executeSelect(builder.build(), pss, map));
			assertEquals(M3RepositoryException.Type.PREPARED_STATEMENT_SETTER, ex.getType());
		}
		verify(conn).close();
		verify(conn).rollback();
	}

	@Test
	void testExecuteSelectFailsOnExecuteQuery() throws SQLException {
		when(rs.next()).thenReturn(true); // has result
		doThrow(SQLException.class).when(ps).executeQuery();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(map.map(Mockito.any(), Mockito.any())).thenReturn(mock(PojoI.class));

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeSelect(builder.build(), pss, map));
			tx.rollback();
		}
		verify(conn).close();
		verify(conn).rollback();
	}

	@Test
	void testExecuteSelectFailsOnResultsetNext() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		doThrow(SQLException.class).when(rs).next();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(map.map(Mockito.any(), Mockito.any())).thenReturn(mock(PojoI.class));

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeSelect(builder.build(), pss, map));
		}
	}

	@Test
	void testExecuteSelectReturnsNoResult() throws SQLException {
		when(ps.executeQuery()).thenReturn(rs);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(rs.next()).thenReturn(false);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			PojoI p = repo.executeSelect(builder.build(), pss, map);
			Assertions.assertNull(p);
			tx.rollback();
		}
		verify(ps).executeQuery();
		verify(rs).next();
		verify(conn).close();
	}

	// INSERT

	@Test
	void testExecuteInsertFailsOnPrepareStatement() throws SQLException {
		doThrow(SQLException.class).when(conn).prepareStatement(anyString(), anyInt());

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			assertThrows(EventstormTransactionException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}

		verify(conn).close();
	}

	@Test
	void testExecuteInsertFailsOnFillPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(map).insert(ps, pojo);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}

	}

	@Test
	void testExecuteInsertFailsOnExecuteUpdate() throws SQLException {
		
		doThrow(SQLException.class).when(ps).executeUpdate();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}
	}

	@Test
	void testExecuteInsertFailsOnWrongNumberUpdated() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(0);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeInsert(builder.build(), map, pojo));
			tx.rollback();
		}
	}

	// UPDATE

	@Test
	void testExecuteUpdate() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(1);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);

		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
			repo.executeUpdate(builder.build(), map, pojo);
			tx.rollback();
		}
		verify(ps).executeUpdate();
		verify(conn).close();
	}

	@Test
	void testExecuteUpdateFailsOnPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(conn).prepareStatement(anyString(), anyInt());
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			Assertions.assertThrows(EventstormTransactionException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnFillPrepareStatement() throws SQLException {
		
		doThrow(SQLException.class).when(map).update(ps, pojo);
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(MapperException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnExecuteUpdate() throws SQLException {
		
		doThrow(SQLException.class).when(ps).executeUpdate();
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(EventstormSqlException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testExecuteUpdateFailsOnWrongNumberUpdated() throws SQLException {
		
		when(ps.executeUpdate()).thenReturn(0);// success
		when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
		when(conn.createStatement()).thenReturn(mock(Statement.class));
		DefaultRepository repo = new DefaultRepository(db);
		SelectBuilder builder = repo.select(Pojos.FOLDER_ALL);
		builder.from(Pojos.DESCRIPTOR_FOLDER.table());
		try (Transaction tx = db.transactionManager().newTransactionReadOnly()) {
			assertThrows(RuntimeException.class, () -> repo.executeUpdate(builder.build(), map, pojo));
		}
	}

	//@Test
	void testInsert() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		InsertBuilder builder = repo.insert(Pojos.FOLDER_TABLE, Pojos.FOLDER_IDS, Pojos.FOLDER_COLUMNS);
		assertEquals("INSERT INTO folder (id,parent_fk,path,full_path,created_at) VALUES (?,?,?,?,?)", builder.build());
	}

	//@Test
	void testUpdate() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		UpdateBuilder builder = repo.update(Pojos.FOLDER_TABLE, Pojos.FOLDER_COLUMNS, Pojos.FOLDER_IDS);
		assertEquals("UPDATE folder SET parent_fk=?,path=?,full_path=?,created_at=? WHERE id=?", builder.build());
	}

	//@Test
	void testDatabase() {
		Database db = Pojos.DATABASE;
		DefaultRepository repo = new DefaultRepository(db);
		assertEquals(db, repo.database());
	}

	static class DefaultRepository extends Repository {

		DefaultRepository(Database database) {
			super(database);
		}

	}
	
	*/
}
