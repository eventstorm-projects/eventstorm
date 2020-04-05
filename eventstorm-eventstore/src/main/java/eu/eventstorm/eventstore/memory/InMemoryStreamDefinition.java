package eu.eventstorm.eventstore.memory;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static eu.eventstorm.eventstore.StreamDefinitionException.STREAM_PAYLOAD;
import static java.util.function.Function.identity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamDefinitionException;
import eu.eventstorm.eventstore.StreamEvantPayloadDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class InMemoryStreamDefinition implements StreamDefinition {

	private final String name;
	
	private final ImmutableMap<String, InMemoryStreamEvantPayloadDefinition<?>> mapByEventPayloadType;
	private final ImmutableMap<String, InMemoryStreamEvantPayloadDefinition<?>> mapByEventPayloadClass;
	private final ConcurrentHashMap<String, InMemoryStreamEvantPayloadDefinition<?>> cache;
	
	public InMemoryStreamDefinition(String name, List<InMemoryStreamEvantPayloadDefinition<?>> defs) {
		this.name = name;
		this.mapByEventPayloadType = defs.stream().collect(toImmutableMap(InMemoryStreamEvantPayloadDefinition::getPayloadType, identity()));
		this.mapByEventPayloadClass = defs.stream().collect(toImmutableMap( d -> d.getEventPayloadClass().getName(), identity()));
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends EventPayload> StreamEvantPayloadDefinition<T> getStreamEvantPayloadDefinition(String payloadType) {
		return (StreamEvantPayloadDefinition<T>) this.mapByEventPayloadType.get(payloadType);
	}

	@Override
	public <T extends EventPayload> StreamEvantPayloadDefinition<T> getStreamEvantPayloadDefinition(T eventPayload) {
		
		@SuppressWarnings("unchecked")
		StreamEvantPayloadDefinition<T> def = (StreamEvantPayloadDefinition<T>) getFromCache(eventPayload.getClass().getName(), eventPayload.getClass());

		if (def == null) {
			throw new StreamDefinitionException(StreamDefinitionException.Type.INVALID_STREAM_PAYLOAD_CLASS, of(STREAM_PAYLOAD, eventPayload));
		}

		return def;
	}

	private StreamEvantPayloadDefinition<?> getFromCache(String classname, Class<?> clazz) {

		InMemoryStreamEvantPayloadDefinition<?> def = this.cache.get(clazz.getName());
		
		if (def != null) {
			return def;
		}
		
		def = this.mapByEventPayloadClass.get(clazz.getName());
		
		if (def != null) {
			this.cache.putIfAbsent(classname, def);
			return def;
		}
		
		Class<?>[] classes = clazz.getInterfaces();
		
		if (classes != null) {
			for (Class<?> item : classes) {
				def = (InMemoryStreamEvantPayloadDefinition<?>) getFromCache(classname, item);
				if (def != null) {
					return def;
				}
			}
		}
		return null;
	}
	
}
