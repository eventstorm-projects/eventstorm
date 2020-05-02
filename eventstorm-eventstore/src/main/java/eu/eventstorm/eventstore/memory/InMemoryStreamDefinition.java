package eu.eventstorm.eventstore.memory;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static eu.eventstorm.eventstore.StreamDefinitionException.STREAM_EVENT_TYPE;
import static java.util.function.Function.identity;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.AbstractMessage;

import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamDefinitionException;
import eu.eventstorm.eventstore.StreamEventDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryStreamDefinition implements StreamDefinition {

	private final String name;
	
	private final ImmutableMap<String, InMemoryStreamEventDefinition> mapByEventPayloadType;
//	private final ImmutableMap<String, InMemoryStreamEvantPayloadDefinition<?>> mapByEventPayloadClass;
//	private final ConcurrentHashMap<String, InMemoryStreamEvantPayloadDefinition<?>> cache;
//	
	public InMemoryStreamDefinition(String name, List<InMemoryStreamEventDefinition<?>> defs) {
		this.name = name;
		this.mapByEventPayloadType = defs.stream().collect(toImmutableMap(InMemoryStreamEventDefinition::getEventType, identity()));
//		this.mapByEventPayloadClass = defs.stream().collect(toImmutableMap( d -> d.getEventPayloadClass().getName(), identity()));
//		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public String getName() {
		return this.name;
	}

//	@SuppressWarnings("unchecked")
//	//@Override
//	public <T> StreamEvantPayloadDefinition<T> getStreamEvantPayloadDefinition(String payloadType) {
//		//return (StreamEvantPayloadDefinition<T>) this.mapByEventPayloadType.get(payloadType);
//		return null;
//	}

	//@Override
	public <T extends AbstractMessage> StreamEventDefinition getStreamEvantPayloadDefinition(T eventPayload) {
		
//		@SuppressWarnings("unchecked")
//		StreamEvantPayloadDefinition<T> def = (StreamEvantPayloadDefinition<T>) getFromCache(eventPayload.getClass().getName(), eventPayload.getClass());
//
//		if (def == null) {
//			throw new StreamDefinitionException(StreamDefinitionException.Type.INVALID_STREAM_PAYLOAD_CLASS, of(STREAM_PAYLOAD, eventPayload));
//		}
//
//		return def;
		
		return null;
	}

	@Override
	public <T extends AbstractMessage> StreamEventDefinition getStreamEventDefinition(String event) {
		StreamEventDefinition def = (StreamEventDefinition)mapByEventPayloadType.get(event);

		if (def == null) {
			throw new StreamDefinitionException(StreamDefinitionException.Type.NVALID_STREAM_EVENT_TYPE, of(STREAM_EVENT_TYPE, event));
		}

		return def;
	}


//	private StreamEvantPayloadDefinition<?> getFromCache(String classname, Class<?> clazz) {
//
//		InMemoryStreamEvantPayloadDefinition<?> def = this.cache.get(clazz.getName());
//		
//		if (def != null) {
//			return def;
//		}
//		
//		def = this.mapByEventPayloadClass.get(clazz.getName());
//		
//		if (def != null) {
//			this.cache.putIfAbsent(classname, def);
//			return def;
//		}
//		
//		for (Class<?> item : clazz.getInterfaces()) {
//			def = (InMemoryStreamEvantPayloadDefinition<?>) getFromCache(classname, item);
//			if (def != null) {
//				return def;
//			}
//		}
//		return null;
//	}
	
}
