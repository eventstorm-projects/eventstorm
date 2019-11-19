package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

public class CommandDescriptor {

	private final Element element;
	private final List<CommandPropertyDescriptor> properties;
	
	public CommandDescriptor(Element element, List<CommandPropertyDescriptor> properties) {
		this.element = element;
		this.properties = properties;
	}

	 public String fullyQualidiedClassName() {
	        return this.element.asType().toString();
	    }

	public Element element() {
		return element;
	}

	public List<CommandPropertyDescriptor> properties() {
		return properties;
	}

	public String simpleName() {
		String fqcn =  this.element.asType().toString();
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
	}

}
