package eu.eventstorm.page;

import java.util.List;

import eu.eventstorm.util.Strings;

/**
 * Abstract interface for pagination information.
 *
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface PageRequest {

	int getSize();

	int getOffset();

	Filter getFilter();

	void setFilter(Filter filter);

	List<Sort> getSorts();

	EvaluatorDefinition getEvaluator();

	PageRequest next();

	static PageRequestBuilder of(String query, int offset, int size) {
		return new PageRequestBuilder(query, offset, size);
	}

	static PageRequestBuilder of(int offset, int size) {
		return new PageRequestBuilder(Strings.EMPTY, offset, size);
	}


}