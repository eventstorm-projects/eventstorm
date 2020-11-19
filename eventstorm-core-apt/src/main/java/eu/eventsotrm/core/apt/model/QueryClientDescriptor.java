package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryClientDescriptor  extends QueryDescriptor {

	public QueryClientDescriptor(Element element, List<QueryPropertyDescriptor> properties) {
		super(element, properties);
	}
	

}
