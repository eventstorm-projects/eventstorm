package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.desc.SqlColumn;

final class DefaultOverPartition implements OverPartition {

	private final SqlColumn column;
	private final Order order;
	private final String alias;
	
	public DefaultOverPartition(SqlColumn column, Order order, String alias) {
		this.column = column;
		this.order = order;
		this.alias = alias;
	}

	@Override
	public String build(Dialect dialect, boolean alias) {
		
		StringBuilder builder = new StringBuilder(128);
		builder.append("OVER (PARTITION BY ");
		
		// add column
		if (alias) {
            builder.append(column.table().alias()).append('.');
        }
		builder.append(column.name());
		
		if (order != null) {
			builder.append(" ORDER BY ");
			if (alias) {
	            builder.append(order.column().table().alias()).append('.');
	        }
			builder.append(order.column().name());
			builder.append(' ');
			builder.append(order.type().name());
		}
		
		builder.append(") ");
		builder.append(this.alias);
		
		return builder.toString();
	}

}
