package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

public class EmbeddedCommandDescriptor extends AbstractCommandDescriptor{

	public EmbeddedCommandDescriptor(Element element, List<PropertyDescriptor> properties) {
		super(element, properties);
	}

}