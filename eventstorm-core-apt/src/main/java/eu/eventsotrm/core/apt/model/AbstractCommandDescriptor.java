package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

public class AbstractCommandDescriptor implements Descriptor {

	private final Element element;
	private final List<PropertyDescriptor> properties;
	
	public AbstractCommandDescriptor(Element element, List<PropertyDescriptor> properties) {
		this.element = element;
		this.properties = properties;
	}

	 public String fullyQualidiedClassName() {
	        return this.element.asType().toString();
	    }

	public Element element() {
		return element;
	}

	public List<PropertyDescriptor> properties() {
		return properties;
	}

	public String simpleName() {
		String fqcn =  this.element.asType().toString();
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()+" : ").append(element);
        for (PropertyDescriptor property : properties) {
            builder.append("\n\t").append(property);
        }
        return builder.toString();
    }

}
