package eu.eventstorm.page;

import java.util.List;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PageRequestImpl implements PageRequest {

	private final String query;
    private final int offset;
    private final int size;
    private final List<Filter> filters;
    private final List<Sort> sorts;
    private final EvaluatorDefinition evaluator;

	PageRequestImpl(String query, int offset, int size, List<Filter> filters, List<Sort> sorts, EvaluatorDefinition evaluator) {
		this.query = query;
        this.offset = offset;
        this.size = size;
        this.filters = filters;
        this.sorts = sorts;
        this.evaluator = evaluator;
	}

	@Override
	public int getOffset() {
		return this.offset;
	}

	@Override
	public int getSize() {
		return this.size;
	}
	
	@Override
	public List<Filter> getFilters() {
		return this.filters;
	}

	@Override
	public List<Sort> getSorts() {
		return this.sorts;
	}

	@Override
	public EvaluatorDefinition getEvaluator() {
		return this.evaluator;
	}

	@Override
	public PageRequest next() {
		return new PageRequestImpl(this.query, offset + size, size, filters, sorts, evaluator);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(true)
				.append("query", query)
				.append("offset", offset)
				.append("size", size)
				.append("filter", this.filters)
				.append("sorts", this.sorts)
				.toString();
	}

}
