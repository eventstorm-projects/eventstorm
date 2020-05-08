package eu.eventsotrm.core.apt.model;

import javax.lang.model.element.Element;

import eu.eventstorm.annotation.EventEvolution;

public class EventEvolutionDescriptor implements Descriptor {

    private final Element element;
    private final EventEvolution eventEvolution;

    public EventEvolutionDescriptor(Element element) {
        this.element = element;
        this.eventEvolution = element.getAnnotation(EventEvolution.class);
    }

    public String fullyQualidiedClassName() {
        return this.element.asType().toString();
    }

    public Element element() {
        return element;
    }
    
    public EventEvolution eventEvolution() {
    	return this.eventEvolution;
    }

    public String simpleName() {
        String fqcn = this.element.asType().toString();
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventEvolutionDescriptor : ").append(element);
        builder.append(" eventEvolution=[").append(eventEvolution).append("]");
//        for (PropertyDescriptor property : properties) {
//            builder.append("\n\t").append(property);
//        }
        return builder.toString();
    }

}
