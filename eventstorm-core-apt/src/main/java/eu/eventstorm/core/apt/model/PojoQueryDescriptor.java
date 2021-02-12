package eu.eventstorm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PojoQueryDescriptor  extends QueryDescriptor {

	public PojoQueryDescriptor(Element element, List<QueryPropertyDescriptor> properties) {
		super(element, properties);
	}
	

}
