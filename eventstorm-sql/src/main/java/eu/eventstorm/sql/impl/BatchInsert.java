package eu.eventstorm.sql.impl;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.EventstormRepositoryException.PARAM_POJO;
import static eu.eventstorm.sql.EventstormRepositoryException.PARAM_SQL;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_ADD;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.INSERT_MAPPER;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.jdbc.InsertMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class BatchInsert<E> extends AbstractBatch<E> {

	private final InsertMapper<E> im;

	public BatchInsert(Database database, SqlQuery query, InsertMapper<E> im) {
		super(database, query);
		this.im = im;
	}

	@Override
	public void add(E pojo) {
		PreparedStatement ps = getTransactionQueryContext().preparedStatement();
		try {
			im.insert(getDatabase().dialect(), ps, pojo);
		} catch (SQLException cause) {
			throw getTransactionQueryContext().exception(new EventstormRepositoryException(INSERT_MAPPER, of(PARAM_SQL, getQuery(), PARAM_POJO, pojo), cause));
		}

		try {
			ps.addBatch();
		} catch (SQLException cause) {
			throw getTransactionQueryContext().exception(new EventstormRepositoryException(BATCH_ADD, of(PARAM_SQL, getQuery(), PARAM_POJO, pojo), cause));
		}
		getCounter().incrementAndGet();
	}

}
