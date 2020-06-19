package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.SqlQuery;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface TransactionContext {

	TransactionQueryContext read(SqlQuery query);

	TransactionQueryContext write(SqlQuery query);
	
	TransactionQueryContext writeAutoIncrement(SqlQuery query);

}