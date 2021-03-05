package eu.eventstorm.core.apt.model;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryClientServiceMethod;

import javax.lang.model.element.ExecutableElement;

public final class QueryClientServiceMethodDescriptor {

    private final ExecutableElement method;
    private final CqrsQueryClientServiceMethod annotation;
    private final ImmutableList<Parameter> parameters;

    public QueryClientServiceMethodDescriptor(ExecutableElement executableElement, CqrsQueryClientServiceMethod annotation, ImmutableList<Parameter> parameters) {
        this.method = executableElement;
        this.annotation = annotation;
        this.parameters = parameters;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public CqrsQueryClientServiceMethod getAnnotation() {
        return annotation;
    }

    public ImmutableList<Parameter> getParameters() {
        return parameters;
    }

    public static class Parameter {
        private final String name;
        private final String type;
        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
    }

}