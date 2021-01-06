package eu.eventsotrm.core.apt.model;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseTableQueryDescriptor extends QueryDescriptor {

	public DatabaseTableQueryDescriptor(Element element, List<QueryPropertyDescriptor> properties) {
		super(element, properties);
	}
	

}
