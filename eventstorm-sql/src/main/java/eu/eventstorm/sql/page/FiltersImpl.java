package eu.eventstorm.sql.page;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class FiltersImpl implements Filters{
	
	private final List<Filter> filters;

	public FiltersImpl(List<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public ImmutableList<Expression> toExpressions() {
		ImmutableList.Builder<Expression> builder = ImmutableList.builder();
		this.filters.forEach(f -> builder.add(f.getExpression()));
		return builder.build();
	}

	@Override
	public int size() {
		return filters.size();
	}

	@Override
	public PreparedStatementSetter composeWith(SqlQueryPageable query, PreparedStatementSetter pss) {
		if (filters.isEmpty()) {
			return pss;
		}
		return ps -> {
			pss.set(ps);
			int i = query.getIndex();
			for (Filter filter : filters) {
				PreparedStatementIndexSetter psis = filter.getPreparedStatementIndexSetter();
				if (psis != null) {
					i = psis.set(ps, i);
				}
			}
		};
	}

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("filters", this.filters)
				.toString();
	}

	@Override
	public void forEach(Consumer<Filter> consumer) {
		this.filters.forEach(consumer);
	}
	
	@Override
	public void add(String property, String operator, String value, Expression expression, PreparedStatementIndexSetter psis) {
		this.filters.add(new FilterImpl(property, operator, value, expression, psis));
	}

}