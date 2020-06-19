package eu.eventstorm.sql.id;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.id.IdentifierException.PARAM_SEQUENCE;
import static eu.eventstorm.sql.id.IdentifierException.Type.SEQUENCE_EXECUTE_QUERY;
import static eu.eventstorm.sql.id.IdentifierException.Type.SEQUENCE_EXTRACT;
import static eu.eventstorm.sql.id.IdentifierException.Type.SEQUENCE_NO_RESULT;
import static eu.eventstorm.sql.id.IdentifierException.Type.SEQUENCE_RESULT_SET_NEXT;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.builder.SqlQueryImpl;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.impl.TransactionQueryContext;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SequenceGenerator<T> implements Identifier<T> {

	private final Database database;

	private final SqlQuery sequence;

	public SequenceGenerator(Database database, SqlSequence sequence) {
		this.database = database;
		String sql = database.dialect().nextVal(sequence);
		this.sequence = new SqlQueryImpl(sql);
	}
	
	public final Database getDatabase() {
	    return this.database;
	}

	public final T next() {
		try (TransactionQueryContext tqc = database.transactionManager().context().read(this.sequence)) {
			try (ResultSet rs = tqc.preparedStatement().executeQuery()) {
				return next(rs);
			} catch (SQLException cause) {
				throw tqc.exception(new IdentifierException(SEQUENCE_EXECUTE_QUERY, of(PARAM_SEQUENCE, sequence), cause));
			}
		}
	}

	private T next(ResultSet rs) {

		boolean next;
		try {
			next = rs.next();
		} catch (SQLException cause) {
			throw new IdentifierException(SEQUENCE_RESULT_SET_NEXT, of(PARAM_SEQUENCE, sequence), cause);
		}

		if (next) {
			try {
				return extractResult(rs);
			} catch (SQLException cause) {
				throw new IdentifierException(SEQUENCE_EXTRACT, of(PARAM_SEQUENCE, sequence), cause);
			}
		} else {
			throw new IdentifierException(SEQUENCE_NO_RESULT, of(PARAM_SEQUENCE, sequence));
		}
	}

	protected abstract T extractResult(ResultSet rs) throws SQLException;

}
