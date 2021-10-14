package eu.eventstorm.core.apt.model;

import javax.lang.model.element.Element;
import java.util.List;

public final class SagaCommandDescriptor extends AbstractCommandDescriptor{

	public SagaCommandDescriptor(Element element, List<PropertyDescriptor> properties) {
		super(element, properties);
	}

}