package eu.eventstorm.sql.id;

import static com.google.common.collect.ImmutableMap.of;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SequenceGenerator<T> implements Identifier<T> {

	private final Database database;

	private final String sequence;

	public SequenceGenerator(Database database, SqlSequence sequence) {
		this.database = database;
		this.sequence = database.dialect().nextVal(sequence);
	}

	public final T next() {
		PreparedStatement ps = database.transactionManager().context().read(this.sequence);

		try (ResultSet rs = ps.executeQuery()) {
			return next(rs);
		} catch (SQLException cause) {
			throw new IdentifierException(IdentifierException.Type.SEQUENCE_EXECUTE_QUERY, of("sequence", sequence), cause);
		}
	}

	private T next(ResultSet rs) {
		
		boolean next; 
		try {
			next = rs.next();
		} catch (SQLException cause) {
			throw new IdentifierException(IdentifierException.Type.SEQUENCE_RESULT_SET_NEXT, of("sequence", sequence), cause);
		}
		
		if (next) {
			try {
				return extractResult(rs);
			} catch (SQLException cause) {
				throw new IdentifierException(IdentifierException.Type.SEQUENCE_EXTRACT, of("sequence", sequence), cause);
			}
		} else {
			throw new IdentifierException(IdentifierException.Type.SEQUENCE_NO_RESULT, of("sequence", sequence));
		}
	}

	protected abstract T extractResult(ResultSet rs) throws SQLException;

	protected String getSequenceAsSql() {
		return this.sequence;
	}
}
