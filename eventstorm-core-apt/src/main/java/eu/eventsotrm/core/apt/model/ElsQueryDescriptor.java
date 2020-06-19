package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

import eu.eventstorm.annotation.CqrsQueryElsIndex;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsQueryDescriptor extends QueryDescriptor {

	public ElsQueryDescriptor(Element element, List<QueryPropertyDescriptor> properties) {
		super(element, properties);
	}
	
	public CqrsQueryElsIndex indice() {
		return element().getAnnotation(CqrsQueryElsIndex.class);
	}

}
