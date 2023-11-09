package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SubSelects {

	private SubSelects() {
	}

	public static SubSelect from(SqlQuery query, String alias) {
		return new SubSelectImpl(query, alias);
	}
	
	public static SubSelect from(SqlQuery query) {
		return new SubSelectImpl(query, Strings.EMPTY);
	}

}
