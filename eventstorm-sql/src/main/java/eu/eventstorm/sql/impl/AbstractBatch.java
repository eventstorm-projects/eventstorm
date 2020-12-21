package eu.eventstorm.sql.impl;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.sql.EventstormRepositoryException.PARAM_SIZE;
import static eu.eventstorm.sql.EventstormRepositoryException.PARAM_SQL;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_EXECUTE_QUERY;
import static eu.eventstorm.sql.EventstormRepositoryException.Type.BATCH_RESULT;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.EventstormRepositoryException;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.jdbc.Batch;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class AbstractBatch<E> implements Batch<E> {

	private final Database database;
    private final SqlQuery query;
    private final TransactionQueryContext tqc;
    private final AtomicInteger counter;

    protected AbstractBatch(Database database, SqlQuery query) {
    	this.database = database;
        this.query = query;
        this.tqc = database.transactionManager().context().write(query);
        this.counter = new AtomicInteger(0);
    }

	@Override
	public final void close() {
        try {
            doClose();
        } finally {
            tqc.close();
        }

	}

	protected final Database getDatabase() {
		return this.database;
	}
	
	protected final SqlQuery getQuery() {
		return this.query;
	}
	
	protected final AtomicInteger getCounter() {
		return this.counter;
	}
	
	protected final TransactionQueryContext getTransactionQueryContext() {
		return this.tqc;
	}
	
	private void doClose() {
        int[] vals;

		try {
			vals = tqc.preparedStatement().executeBatch();
		} catch (SQLException cause) {
			throw tqc.exception(new EventstormRepositoryException(BATCH_EXECUTE_QUERY, of(PARAM_SQL, query), cause));
		}

		if (vals.length != getCounter().get()) {
			throw tqc.exception(new EventstormRepositoryException(BATCH_RESULT, of(PARAM_SQL, query, PARAM_SIZE, vals.length)));
		}
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] != 1) {
				throw tqc.exception(new EventstormRepositoryException(BATCH_RESULT, of(PARAM_SQL, query, "item", i, "return", vals[i])));
			}
		}
	}

}
