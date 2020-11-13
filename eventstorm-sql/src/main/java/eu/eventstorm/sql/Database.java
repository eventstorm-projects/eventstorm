package eu.eventstorm.sql;

import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Database extends AutoCloseable {

    Dialect dialect();

    TransactionManager transactionManager();
    
    RawSqlExecutor rawSqlExecutor();

    Module getModule(SqlTable table);
    
    Module getModule(SqlSequence sequence);
    
    JsonMapper jsonMapper();

	@Override
	void close();
    
}