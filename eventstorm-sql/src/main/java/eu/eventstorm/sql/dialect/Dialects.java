package eu.eventstorm.sql.dialect;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;

public final class Dialects {

    private Dialects() {
    }

    public static Dialect h2(Database database) {
        return new H2Dialect(database);
    }
    
    public static Dialect oracle(Database database) {
        return new OracleDialect(database);
    }

    public static Dialect postgres(Database database) {
        return new PostgresDialect(database);
    }

    public static Dialect dialect(Dialect.Name name, Database database) {
        switch (name) {
            case H2:
                return h2(database);
            case ORACLE:
                return oracle(database);
            case POSTGRES:
                return postgres(database);
            default:
                throw new IllegalStateException("invalid dialect name [" + name + "]");
        }
    }
}
