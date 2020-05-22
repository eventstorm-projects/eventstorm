package eu.eventsotrm.core.apt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

import eu.eventstorm.annotation.EventEvolution;
import io.protostuff.compiler.model.Message;

public class EventEvolutionDescriptor implements Descriptor {

    private final Element element;
    private final EventEvolution eventEvolution;
    private final Map<String, List<Message>> messages = new HashMap<>();

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
    
//    public void add(Message message) {
//		this.messages.add(message);
//	}
    
    public Map<String,List<Message>> getMessages() {
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

	public void add(String proto, Message message) {
		if (!this.messages.containsKey(proto)) {
			this.messages.put(proto, new ArrayList<>());
		} 
		this.messages.get(proto).add(message);
	}

}
