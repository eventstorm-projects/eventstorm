package eu.eventstorm.core.apt.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import eu.eventstorm.annotation.EventEvolution;

public final class EventEvolutionDescriptor implements Descriptor {

    private final Element element;
    private final EventEvolution eventEvolution;
    private final Map<String, List<ProtobufMessage>> messages = new HashMap<>();

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
    
    public Map<String,List<ProtobufMessage>> getMessages() {
    	return this.messages;
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

	public void add(Protobuf protobuf) {
		int index = protobuf.getProto().lastIndexOf('/');
		if (index == -1 ) {
			index = 0;
		} else {
			index++;
		}
		String stream = protobuf.getProto().substring(index, protobuf.getProto().indexOf(".proto")); 
		this.messages.put(stream, protobuf.getMessages());
	}

}
