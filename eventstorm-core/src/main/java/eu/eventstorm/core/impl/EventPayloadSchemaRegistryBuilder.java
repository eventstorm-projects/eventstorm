package eu.eventstorm.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventPayloadSchema;
import eu.eventstorm.core.EventPayloadSchemaRegistry;
import eu.eventstorm.core.annotation.CqrsEventPayload;
import eu.eventstorm.core.impl.EventPayloadSchemaRegistryImpl.EventPayloadDefinition;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventPayloadSchemaRegistryBuilder {

	private final Map<String, EventPayloadDefinitionBuilder<?>> map = new HashMap<>();
	
	public EventPayloadSchemaRegistry build() {
		ImmutableMap.Builder<String, EventPayloadDefinition<?>> builder = ImmutableMap.builder();
		map.forEach((key, definition) -> builder.put(key, definition.build()));
		return new EventPayloadSchemaRegistryImpl(builder.build());
	}

	public <T extends EventPayload> void add(Class<T> eventPayLoadInterface, Serializer<T> serializer, Deserializer<T> deserializer) {

		map.put(eventPayLoadInterface.getName(), new EventPayloadDefinitionBuilder<T>()
				.eventPayloadInterface(eventPayLoadInterface)
				.serializer(serializer)
				.deserializer(deserializer));
		
	}
	
	static final class EventPayloadDefinitionBuilder<T extends EventPayload> {

		private Serializer<T> serializer;
		private Deserializer<T> deserializer;
		private EventPayloadSchema schema;
		
		public EventPayloadDefinitionBuilder<T> serializer(Serializer<T> serializer) {
			this.serializer = serializer;
			return this;
		}

		public EventPayloadDefinitionBuilder<T> deserializer(Deserializer<T> deserializer) {
			this.deserializer = deserializer;
			return this;
		}
		
		public EventPayloadDefinitionBuilder<T> eventPayloadInterface(Class<T> eventPayLoadInterface) {
			
			if (!eventPayLoadInterface.isInterface()) {
				// TODO return exception
			}
			
			
			CqrsEventPayload cqrsEventPayload = eventPayLoadInterface.getAnnotation(CqrsEventPayload.class);

			if (cqrsEventPayload != null) {
				// TODO trown exception
			}
			
			String type = cqrsEventPayload.type();
			if (Strings.isEmpty(type)) {
			    type = eventPayLoadInterface.getName();
			}
			
			this.schema = new EventPayloadSchemaImpl(type, 1);	
			return this;
		}
		
		public EventPayloadDefinition<T> build() {
			return new EventPayloadDefinition<T>(schema, serializer, deserializer);
		}
	}
	
}