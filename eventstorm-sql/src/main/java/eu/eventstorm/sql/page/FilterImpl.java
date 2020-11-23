package eu.eventstorm.sql.page;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class FilterImpl implements Filter {

	private final String property;
	private final String operator;
	private final String value;
	private final FilterEvaluator evalutor;
	
	public FilterImpl(String property, String operator, String value, FilterEvaluator evalutor) {
		this.property = property;
		this.operator = operator;
		this.value = value;
		this.evalutor = evalutor;
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("property", property)
				.append("operator", operator)
				.append("value", value)
				.toString();
	}

	@Override
	public FilterEvaluator getEvalutor() {
		return this.evalutor;
	}

}