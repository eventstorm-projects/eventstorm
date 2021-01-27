package eu.eventstorm.page;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequestBuilder {

	private final String query;
	private final int offset;
	private final int size;
	private final List<Filter> filters = new ArrayList<>(4);
	private final List<Sort> sorts = new ArrayList<>(4);
	private EvaluatorDefinition evaluator;
	
	PageRequestBuilder(String query, int offset, int size) {
		this.query = query;
		this.offset = offset;
		this.size = size;
	}
	  
	public PageRequest build() {
		return new PageRequestImpl(query, offset, size, filters, sorts, evaluator);
	}

	public PageRequestBuilder withFilter(String property, Operator operator, String raw, List<String> values) {
		filters.add(new FilterImpl(property, operator, raw, values));
		return this;
	}

	public PageRequestBuilder withFilter(String property, Operator operator, String raw) {
		filters.add(new FilterImpl(property, operator, raw, ImmutableList.of(raw)));
		return this;
	}

	public PageRequestBuilder withSort(Sort sort) {
		this.sorts.add(sort);
		return this;
	}

	public PageRequestBuilder withEvaluator(EvaluatorDefinition evaluator) {
		this.evaluator = evaluator;
		return this;
	}
}
