package eu.eventstorm.core.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventPayloadRegistry;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadRegistryImpl implements EventPayloadRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventPayloadRegistryImpl.class);
	
	private final ImmutableMap<String, EventPayloadDefinition<?>> registry;
	
	private final ConcurrentMap<String, String> cache;

	public EventPayloadRegistryImpl(ImmutableMap<String, EventPayloadDefinition<?>> registry) {
		this.registry = registry;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public String getPayloadType(EventPayload payload) {
		
		String cacheKey = cache.get(payload.getClass().getName());

		if (cacheKey == null) {
			cacheKey = buildCacheKey(payload);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getSchema({}) -> cacheKey=[{}]", payload.getClass().getName(), cacheKey);
			}
			
			this.cache.put(payload.getClass().getName(), cacheKey);
		}
		
		if (cacheKey == null) {
			// TODO THROWS eception
		}
		
		return this.registry.get(cacheKey).getPayloadType();
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T extends EventPayload> Deserializer<T> getDeserializer(String payloadType) {
	    if (LOGGER.isDebugEnabled()) {
	        LOGGER.debug("getDeserializer({})", payloadType);
	    }
		return (Deserializer<T>) this.registry.get(payloadType).getDeserializer();
	}

	@Override
	public Serializer<EventPayload> getSerializer(String payloadType) {
		return null;
	}

	private String buildCacheKey(EventPayload payload) {

		Class<? extends EventPayload> c = payload.getClass();

		String key = payload.getClass().getName();

		if (this.registry.containsKey(key)) {
			return key;
		}

		for (Class<?> item : c.getInterfaces()) {
			if (this.registry.containsKey(item.getName())) {
				return item.getName();
			}
		}

		return null;
	}

	
	static final class EventPayloadDefinition<T extends EventPayload> {

		private final Serializer<T> serializer;
		private final Deserializer<T> deserializer;
		private final String payloadType;
		
		public EventPayloadDefinition(String payloadType, Serializer<T> serializer, Deserializer<T> deserializer) {
			this.serializer = serializer;
			this.deserializer = deserializer;
			this.payloadType = payloadType;
		}

		public String getPayloadType() {
			return this.payloadType;
		}

		public Serializer<T> getSerializer() {
			return serializer;
		}

		public Deserializer<T> getDeserializer() {
			return deserializer;
		}
		
	}




}
