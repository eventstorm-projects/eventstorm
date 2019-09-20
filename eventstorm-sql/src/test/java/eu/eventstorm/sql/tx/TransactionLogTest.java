package eu.eventstorm.sql.tx;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.impl.DatabaseImpl;
import eu.eventstorm.sql.model.ex001.AbstractStudentRepository;
import eu.eventstorm.sql.model.ex001.Student;
import eu.eventstorm.sql.model.ex001.StudentImpl;

class TransactionLogTest {

	@Test
	void simpleTest() {
		DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/ex001.sql'", "sa", "");
		Database db = new DatabaseImpl(ds, Dialect.Name.H2, new TransactionManagerImpl(ds), "", new eu.eventstorm.sql.model.ex001.Module("txlog", null));

		AbstractStudentRepository repository = new AbstractStudentRepository(db) {
        };

        try (Transaction tx = db.transactionManager().newTransactionReadWrite()) {
            Student student = new StudentImpl();
            student.setId(1);
            student.setAge(37);
            student.setCode("Code1");
            repository.insert(student);
            tx.commit();
        }

	
	}

}
