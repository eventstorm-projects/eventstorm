package eu.eventstorm.core.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventPayloadSchema;
import eu.eventstorm.core.EventPayloadSchemaRegistry;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadSchemaRegistryImpl implements EventPayloadSchemaRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventPayloadSchemaRegistryImpl.class);
	
	private final ImmutableMap<String, EventPayloadDefinition<?>> registry;

	private final ConcurrentMap<String, String> cache;

	public EventPayloadSchemaRegistryImpl(ImmutableMap<String, EventPayloadDefinition<?>> registry) {
		this.registry = registry;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public EventPayloadSchema getSchema(EventPayload payload) {
		
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
		
		return this.registry.get(cacheKey).getSchema();
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T extends EventPayload> Deserializer<T> getDeserializer(String schema, int schemaVersion) {
		return (Deserializer<T>) this.registry.get("toto").getDeserializer();
	}

	@Override
	public Serializer<EventPayload> getSerializer(String schema, int schemaVersion) {
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
		private final EventPayloadSchema schema;
		
		public EventPayloadDefinition(EventPayloadSchema schema, Serializer<T> serializer, Deserializer<T> deserializer) {
			this.serializer = serializer;
			this.deserializer = deserializer;
			this.schema = schema;
		}

		public EventPayloadSchema getSchema() {
			return this.schema;
		}

		public Serializer<T> getSerializer() {
			return serializer;
		}

		public Deserializer<T> getDeserializer() {
			return deserializer;
		}
		
	}
}
