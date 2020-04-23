package eu.eventstorm.sql.model.ex001;

import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.ALL;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.CODE;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.COLUMNS;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.ID;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.IDS;
import static eu.eventstorm.sql.model.ex001.StudentDescriptor.TABLE;

import java.util.stream.Stream;

import eu.eventstorm.sql.jdbc.PreparedStatementSetter;
import eu.eventstorm.sql.jdbc.PreparedStatementSetters;

public abstract class AbstractStudentRepository extends eu.eventstorm.sql.Repository {

	public static final eu.eventstorm.sql.jdbc.Mapper<Student> STUDENT = new StudentMapper();

	private final String findById;
	private final String findByBusinessKey;
	private final String findByIdForUpdate;
	private final String findAll;
	private final String insert;
	private final String update;

	protected AbstractStudentRepository(eu.eventstorm.sql.Database database) {
		super(database);
		this.findById = select(ALL).from(TABLE).where(eq(ID)).build();
		this.findAll = select(ALL).from(TABLE).build();
		this.findByIdForUpdate = select(ALL).from(TABLE).where(eq(ID)).forUpdate().build();
		this.findByBusinessKey = select(ALL).from(TABLE).where(eq(CODE)).build();
		this.insert = insert(TABLE, IDS, COLUMNS).build();
		this.update = update(TABLE, COLUMNS, IDS).build();
	}

	public final eu.eventstorm.sql.model.ex001.Student findById(int id) {
		return executeSelect(this.findById, ps -> {
			ps.setInt(1, id);
		},STUDENT);
	}

	public final eu.eventstorm.sql.model.ex001.Student findByIdForUpdate(int id) {
		return executeSelect(this.findByIdForUpdate, ps -> ps.setInt(1, id), STUDENT);
	}

	public final void insert(eu.eventstorm.sql.model.ex001.Student pojo) {
		// set create timestamp
		pojo.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

		// execute insert
		executeInsert(this.insert, STUDENT, pojo);
	}

	public final void update(eu.eventstorm.sql.model.ex001.Student pojo) {
		// execute update
		executeUpdate(this.update, STUDENT, pojo);
	}

	public final eu.eventstorm.sql.model.ex001.Student findByBusinessKey(java.lang.String code) {
		return executeSelect(this.findByBusinessKey, ps -> ps.setString(1, code), STUDENT);
	}
	
	public final Stream<Student> findAll() {
		return stream(findAll, PreparedStatementSetters.noParameter(), STUDENT);
	}
}