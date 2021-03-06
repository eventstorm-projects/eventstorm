package eu.eventstorm.sql.spring.ex001;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.ALL;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.CODE;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.COLUMNS;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.ID;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.IDS;
import static eu.eventstorm.sql.spring.ex001.StudentDescriptor.TABLE;

import eu.eventstorm.sql.SqlQuery;

public abstract class AbstractStudentRepository extends eu.eventstorm.sql.Repository {

	public static final eu.eventstorm.sql.jdbc.Mapper<Student> STUDENT = new StudentMapper();

	private final SqlQuery findById;
	private final SqlQuery findByBusinessKey;
	private final SqlQuery findByIdForUpdate;
	private final SqlQuery insert;
	private final SqlQuery update;

	protected AbstractStudentRepository(eu.eventstorm.sql.Database database) {
		super(database);
		this.findById = select(ALL).from(TABLE).where(eq(ID)).build();
		this.findByIdForUpdate = select(ALL).from(TABLE).where(eq(ID)).forUpdate().build();
		this.findByBusinessKey = select(ALL).from(TABLE).where(eq(CODE)).build();
		this.insert = insert(TABLE, IDS, COLUMNS).build();
		this.update = update(TABLE, COLUMNS, IDS).build();
	}

	public final eu.eventstorm.sql.spring.ex001.Student findById(int id) {
		return executeSelect(this.findById, ps -> {
			ps.setInt(1, id);
		},STUDENT);
	}

	public final eu.eventstorm.sql.spring.ex001.Student findByIdForUpdate(int id) {
		return executeSelect(this.findByIdForUpdate, ps -> ps.setInt(1, id), STUDENT);
	}

	public final void insert(Student pojo) {
		// set create timestamp
		pojo.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

		// execute insert
		executeInsert(this.insert, STUDENT, pojo);
	}

	public final void update(Student pojo) {
		// execute update
		executeUpdate(this.update, STUDENT, pojo);
	}

	public final Student findByBusinessKey(java.lang.String code) {
		return executeSelect(this.findByBusinessKey, ps -> ps.setString(1, code), STUDENT);
	}
}