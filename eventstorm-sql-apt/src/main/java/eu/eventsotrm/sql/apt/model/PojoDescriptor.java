package eu.eventsotrm.sql.apt.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;

import eu.eventstorm.sql.annotation.BusinessKey;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.Table;

public final class PojoDescriptor {

    private final Element element;
    private final List<PojoPropertyDescriptor> propertyDescriptors;
    private final List<PojoPropertyDescriptor> idDescriptors;
    private final PojoPropertyDescriptor version;

    public PojoDescriptor(Element element, List<PojoPropertyDescriptor> idDescriptors, PojoPropertyDescriptor version,List<PojoPropertyDescriptor> propertyDescriptors) {
        this.element = element;
        this.idDescriptors = idDescriptors;
        this.propertyDescriptors = propertyDescriptors;
        this.version = version;
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

    public List<PojoPropertyDescriptor> properties() {
        return this.propertyDescriptors;
    }

    public List<PojoPropertyDescriptor> ids() {
        return this.idDescriptors;
    }

     public PojoPropertyDescriptor version() {
        return this.version;
    }

    public List<PojoPropertyDescriptor> businessKeys() {
        return propertyDescriptors.stream()
                .filter(ppd -> ppd.getter().getAnnotation(BusinessKey.class) != null)
                .collect(Collectors.toList());
    }

    public Table getTable() {
    	return this.element.getAnnotation(Table.class);
    }

    public JoinTable getJoinTable() {
    	return this.element.getAnnotation(JoinTable.class);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PojoDescriptor [element=");
		builder.append(element);
		builder.append("]");
		this.idDescriptors.forEach(ppd -> {
			builder.append("\n\t\tid [").append(ppd).append("]");
		});

		this.propertyDescriptors.forEach(ppd -> {
			builder.append("\n\t\tproperty [").append(ppd).append("]");
		});
		return builder.toString();
	}


}
