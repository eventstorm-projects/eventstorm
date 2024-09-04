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
    private Filter filter;
    private final List<Sort> sorts;
    private final EvaluatorDefinition evaluator;

	PageRequestImpl(String query, int offset, int size, Filter filter, List<Sort> sorts, EvaluatorDefinition evaluator) {
		this.query = query;
        this.offset = offset;
        this.size = size;
        this.filter = filter;
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
	public Filter getFilter() {
		return this.filter;
	}

	@Override
	public void setFilter(Filter filter) {
		this.filter = (filter == null) ? EmptyFilter.INSTANCE : filter;
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
		return new PageRequestImpl(this.query, offset + size, size, filter == null ? EmptyFilter.INSTANCE : filter, sorts, evaluator);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(false)
				.append("query", query)
				.append("offset", offset)
				.append("size", size)
				.append("filter", this.filter)
				.append("sorts", this.sorts)
				.toString();
	}

}
