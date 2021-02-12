package eu.eventstorm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

public final class CommandDescriptor extends AbstractCommandDescriptor{

	public CommandDescriptor(Element element, List<PropertyDescriptor> properties) {
		super(element, properties);
	}

}