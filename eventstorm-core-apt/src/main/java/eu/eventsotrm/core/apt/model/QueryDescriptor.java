package eu.eventsotrm.core.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

public abstract class QueryDescriptor implements Descriptor {

	private final Element element;
	private final List<QueryPropertyDescriptor> properties;

	public QueryDescriptor(Element element, List<QueryPropertyDescriptor> properties) {
		this.element = element;
		this.properties = properties;
	}

	public String fullyQualidiedClassName() {
		return this.element.asType().toString();
	}

	public Element element() {
		return element;
	}

	public List<QueryPropertyDescriptor> properties() {
		return properties;
	}

	public String simpleName() {
		String fqcn = this.element.asType().toString();
		return fqcn.substring(fqcn.lastIndexOf('.') + 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryDescriptor : ").append(element);
		for (QueryPropertyDescriptor property : properties) {
			builder.append("\n\t").append(property);
		}
		return builder.toString();
	}

}
