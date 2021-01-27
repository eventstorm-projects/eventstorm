package eu.eventstorm.page;

import eu.eventstorm.util.ToStringBuilder;

import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class FilterImpl implements Filter {

	private final String property;
	private final Operator operator;
	private final String raw;
	private final List<String> values;
	
	public FilterImpl(String property, Operator operator, String raw, List<String> values) {
		this.property = property;
		this.operator = operator;
		this.raw = raw;
		this.values = values;
	}

	@Override
	public String getProperty() {
		return this.property;
	}

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public String getRaw() {
		return raw;
	}

	@Override
	public List<String> getValues() {
		return this.values;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("property", property)
				.append("operator", operator)
				.append("raw", raw)
				.append("values", values)
				.toString();
	}

}