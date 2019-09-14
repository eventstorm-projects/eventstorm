package eu.eventsotrm.sql.apt.flyway;

import eu.eventstorm.sql.annotation.Database;

final class FlywayDialects {

	private FlywayDialects() {
	}
	
	static FlywayDialect get(Database database) {
		
		switch (database) {
		case H2:
			return FlywayDialectH2.INSTANCE;
		case ORACLE:
			return FlywayDialectH2.INSTANCE;
		case POSTGRES:
			return FlywayDialectH2.INSTANCE;
		}
		
		throw new IllegalStateException();
	}
}
