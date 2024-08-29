package eu.eventstorm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

import eu.eventstorm.annotation.CqrsQueryElsIndex;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElsQueryDescriptor extends QueryDescriptor {

	private final QueryPropertyDescriptor id;

	public ElsQueryDescriptor(Element element,  QueryPropertyDescriptor id, List<QueryPropertyDescriptor> properties) {
		super(element, properties);
        this.id = id;
    }
	
	public CqrsQueryElsIndex indice() {
		return element().getAnnotation(CqrsQueryElsIndex.class);
	}

	public QueryPropertyDescriptor getId() {
		return id;
	}

	public String getPackage() {
		String fcqn = fullyQualidiedClassName();
		return fcqn.substring(0, fcqn.lastIndexOf('.'));
	}

}
