package eu.eventstorm.sql.apt.flyway;

import eu.eventstorm.sql.annotation.Db;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class FlywayDialects {

	private FlywayDialects() {
	}
	
	static FlywayDialect get(Db database) {
		
		switch (database) {
		case H2:
			return FlywayDialectH2.INSTANCE;
		case ORACLE:
			return FlywayDialectOracle.INSTANCE;
		case POSTGRES:
			return FlywayDialectPostgres.INSTANCE;
		}
		
		throw new IllegalStateException();
	}
}
