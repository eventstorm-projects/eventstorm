package eu.eventstorm.core.apt.model;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryClientService;

import javax.lang.model.element.Element;

public final class QueryClientServiceDescriptor implements Descriptor {

    private final Element element;
    private final ImmutableList<QueryClientServiceMethodDescriptor> methods;
    private final CqrsQueryClientService annotation;

    public QueryClientServiceDescriptor(Element element, CqrsQueryClientService annotation, ImmutableList<QueryClientServiceMethodDescriptor> methods) {
        this.element = element;
        this.methods = methods;
        this.annotation = annotation;
    }

    public String fullyQualifiedClassName() {
        return this.element.asType().toString();
    }

    @Override
    public Element element() {
        return this.element;
    }

    public ImmutableList<QueryClientServiceMethodDescriptor> getMethods() {
        return methods;
    }

    public CqrsQueryClientService getAnnotation() {
        return annotation;
    }
}