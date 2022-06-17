package eu.eventstorm.sql.jdbc;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PreparedStatementSetters {

    private static final PreparedStatementSetter NO_PARAMETER = ps -> {};
    
    private PreparedStatementSetters() {
    }
    
    public static PreparedStatementSetter noParameter() {
    	return NO_PARAMETER;
    }
    
    public static PreparedStatementSetter setSingleString(String value) {
    	return ps -> ps.setString(1, value);
    }

    public static PreparedStatementSetter setSingleUuid(Dialect dialect, String value) {
        return ps -> dialect.setPreparedStatement(ps, 1, value);
    }
    
}