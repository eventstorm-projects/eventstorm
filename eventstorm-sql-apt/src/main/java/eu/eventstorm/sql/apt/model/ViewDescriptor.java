package eu.eventstorm.sql.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

import eu.eventstorm.sql.annotation.View;

public final class ViewDescriptor implements Desc {

    private final Element element;
    private final List<ViewPropertyDescriptor> propertyDescriptors;

    public ViewDescriptor(Element element, List<ViewPropertyDescriptor> propertyDescriptors) {
        this.element = element;
        this.propertyDescriptors = propertyDescriptors;
    }

    public String getPackage() {
    	String fcqn = fullyQualidiedClassName();
    	return fcqn.substring(0, fcqn.lastIndexOf('.'));
    }

    public String fullyQualidiedClassName() {
        return this.element.asType().toString();
    }

    public String simpleName() {
        String fqcn =  this.element.asType().toString();
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }

    public Element element() {
        return this.element;
    }

    public List<ViewPropertyDescriptor> properties() {
        return this.propertyDescriptors;
    }

    public View getView() {
    	return this.element.getAnnotation(View.class);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PojoDescriptor [element=");
		builder.append(element);
		builder.append("]");
		this.propertyDescriptors.forEach(ppd -> {
			builder.append("\n\t\tproperty [").append(ppd).append("]");
		});
		return builder.toString();
	}


}
