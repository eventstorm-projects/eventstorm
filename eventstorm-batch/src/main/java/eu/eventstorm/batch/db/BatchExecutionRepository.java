package eu.eventstorm.batch.db;

import eu.eventstorm.sql.Database;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class BatchExecutionRepository extends AbstractBatchExecutionRepository {

	public BatchExecutionRepository(Database database) {
		super(database);
	}

}
