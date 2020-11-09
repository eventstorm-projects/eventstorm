package eu.eventstorm.sql.expression;

import eu.eventstorm.sql.Dialect;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class RowNumberAggregateFunction implements AggregateFunction {
	
	private final OverPartition overPartition;
	
	public RowNumberAggregateFunction() {
		this.overPartition = null;
	}
	
	public RowNumberAggregateFunction(OverPartition overPartition) {
		this.overPartition = overPartition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String build(Dialect dialect, boolean alias) {
		StringBuilder builder = new StringBuilder(32);
		builder.append("ROW_NUMBER()");
		if (overPartition != null) {
			builder.append(' ');
			builder.append(this.overPartition.build(dialect, alias));
		}
		return builder.toString();
	}

}
