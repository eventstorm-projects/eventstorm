package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Order {

    private final SqlColumn column;
    private final OrderType type;
    
    private Order(SqlColumn column, OrderType type) {
        this.column = column;
        this.type = type;
    }

    public static Order asc(SqlColumn column) {
        return new Order(column, OrderType.ASC);
    }

    public static Order desc(SqlColumn column) {
        return new Order(column, OrderType.DESC);
    }

    public OrderType type() {
        return this.type;
    }
    
    public SqlColumn column() {
        return this.column;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("column", column)
				.append("type", type)
				.toString();
	}
    
}
