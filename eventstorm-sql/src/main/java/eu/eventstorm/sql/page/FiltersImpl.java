package eu.eventstorm.sql.page;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.jdbc.PreparedStatementSetter;

final class FiltersImpl implements Filters{
	
	private final ImmutableList<Filter> list;

	public FiltersImpl(ImmutableList<Filter> list) {
		this.list = list;
	}

	@Override
	public ImmutableList<Expression> toExpressions() {
		return this.list.stream().map(Filter::getExpression).collect(toImmutableList());
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public PreparedStatementSetter composeWith(SqlQueryPageable query, PreparedStatementSetter pss) {
		if (list.isEmpty()) {
			return pss;
		}
		return ps -> {
			pss.set(ps);
			int i = query.getIndex();
			for (Filter filter : list) {
				PreparedStatementIndexSetter psis = filter.getPreparedStatementIndexSetter();
				if (psis != null) {
					i = psis.set(ps, i);
				}
			}
		};
	}

}
