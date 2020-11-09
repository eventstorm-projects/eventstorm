package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.desc.SqlColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class OverPartitions {

	private OverPartitions() {
	}
	
	public static OverPartition by(SqlColumn column, String alias) {
		return new DefaultOverPartition(column, null, alias);
	}
	
	public static OverPartition by(SqlColumn column, Order order, String alias) {
		return new DefaultOverPartition(column, order, alias);
	}
	
}
