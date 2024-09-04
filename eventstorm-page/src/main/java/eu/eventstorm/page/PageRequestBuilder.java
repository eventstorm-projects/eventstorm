package eu.eventstorm.page;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequestBuilder {

	private final String query;
	private final int offset;
	private final int size;
	private Filter filter;
	private final List<Sort> sorts = new ArrayList<>(4);
	private EvaluatorDefinition evaluator;
	
	PageRequestBuilder(String query, int offset, int size) {
		this.query = query;
		this.offset = offset;
		this.size = size;
	}
	  
	public PageRequest build() {
		return new PageRequestImpl(query, offset, size, filter == null ? EmptyFilter.INSTANCE : filter, sorts, evaluator);
	}

	public PageRequestBuilder withFilter(Filter filter) {
		if (this.filter != null) {
			throw new PageRequestException(PageRequestException.Type.FILTER_ALREADY_SET, ImmutableMap.of("filter", filter));
		}
		this.filter = filter;
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
