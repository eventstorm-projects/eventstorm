package eu.eventstorm.sql;

import javax.sql.DataSource;

import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.tx.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Database {

    Dialect dialect();

    TransactionManager transactionManager();

    DataSource dataSource();

    boolean isMonoSchema();

    Module getModule(SqlTable table);
    
    Module getModule(SqlSequence sequence);
    
}