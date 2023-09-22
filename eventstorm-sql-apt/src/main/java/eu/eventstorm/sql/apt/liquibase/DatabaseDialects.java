package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.Db;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseDialects {

	private DatabaseDialects() {
	}
	
	static DatabaseDialect get(Db database) {
		
		switch (database) {
		case H2:
			return DatabaseDialectH2.INSTANCE;
		case ORACLE:
			return DatabaseDialectOracle.INSTANCE;
		case POSTGRES:
			return DatabaseDialectPostgres.INSTANCE;
		}
		
		throw new IllegalStateException();
	}
}
