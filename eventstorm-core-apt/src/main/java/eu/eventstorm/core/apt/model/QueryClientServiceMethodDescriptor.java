package eu.eventstorm.core.apt.model;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.annotation.CqrsQueryClientServiceMethod;
import eu.eventstorm.annotation.Header;
import eu.eventstorm.annotation.Headers;

import javax.lang.model.element.ExecutableElement;

public final class QueryClientServiceMethodDescriptor {

    private final ExecutableElement method;
    private final CqrsQueryClientServiceMethod annotation;
    private final ImmutableList<Parameter> parameters;
    private final ImmutableList<HttpHeader> headers;
    private final ImmutableList<HttpHeaderConsumer> headersConsumers;

    public QueryClientServiceMethodDescriptor(ExecutableElement executableElement, CqrsQueryClientServiceMethod annotation,
                                              ImmutableList<Parameter> parameters,
                                              ImmutableList<HttpHeader> headers,
                                              ImmutableList<HttpHeaderConsumer> headersConsumers) {
        this.method = executableElement;
        this.annotation = annotation;
        this.parameters = parameters;
        this.headers = headers;
        this.headersConsumers = headersConsumers;
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

    public ImmutableList<HttpHeader> getHeaders() {
        return headers;
    }

    public ImmutableList<HttpHeaderConsumer> getHeadersConsumers() {
        return headersConsumers;
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

    public static class HttpHeader extends Parameter {
        private final Header header;
        public HttpHeader(String name, String type, Header header) {
            super(name, type);
            this.header = header;
        }
        public Header getHeader() {
            return header;
        }
    }

    public static class HttpHeaderConsumer extends Parameter {
        private final Headers headers;
        public HttpHeaderConsumer(String name, String type, Headers headers) {
            super(name, type);
            this.headers = headers;
        }
        public Headers getHeaders() {
            return headers;
        }
    }
}