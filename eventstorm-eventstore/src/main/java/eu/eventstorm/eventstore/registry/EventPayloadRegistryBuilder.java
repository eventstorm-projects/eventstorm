package eu.eventstorm.eventstore.registry;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.eventstore.registry.EventPayloadRegistryBuilderException.Type.DUPLICATE_TYPE;
import static eu.eventstorm.eventstore.registry.EventPayloadRegistryBuilderException.Type.MISSING_ANNOTATION_CQRS_EVENTPAYLOAD;
import static eu.eventstorm.eventstore.registry.EventPayloadRegistryBuilderException.Type.NOT_INTERFACE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.EventPayloadRegistry;
import eu.eventstorm.annotation.CqrsEventPayload;
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.Serializer;
import eu.eventstorm.eventstore.registry.EventPayloadRegistryImpl.EventPayloadDefinition;
import eu.eventstorm.util.Strings;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventPayloadRegistryBuilder {

	private final Map<String, EventPayloadDefinitionBuilder<?>> map = new HashMap<>();
	
	private final Set<String> allTypes = new HashSet<>();

	public EventPayloadRegistry build() {
		ImmutableMap.Builder<String, EventPayloadDefinition<?>> builder = ImmutableMap.builder();
		map.forEach((key, definition) -> builder.put(key, definition.build()));
		return new EventPayloadRegistryImpl(builder.build());
	}

	public <T extends EventPayload> void add(Class<T> eventPayLoadInterface, Serializer<T> serializer, Deserializer<T> deserializer) {

		map.put(eventPayLoadInterface.getName(),
		        new EventPayloadDefinitionBuilder<T>().eventPayloadInterface(eventPayLoadInterface).serializer(serializer).deserializer(deserializer));

	}

	final class EventPayloadDefinitionBuilder<T extends EventPayload> {

		private Serializer<T> serializer;
		private Deserializer<T> deserializer;
		private String payloadType;
		private byte payloadVersion;

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
				throw new EventPayloadRegistryBuilderException(NOT_INTERFACE, of("class", eventPayLoadInterface));
			}

			CqrsEventPayload cqrsEventPayload = eventPayLoadInterface.getAnnotation(CqrsEventPayload.class);

			if (cqrsEventPayload == null) {
				throw new EventPayloadRegistryBuilderException(MISSING_ANNOTATION_CQRS_EVENTPAYLOAD, of("class", eventPayLoadInterface));
			}

			String type = cqrsEventPayload.type();
			
			if (Strings.isEmpty(type)) {
				type = eventPayLoadInterface.getName();
			}

			if (allTypes.contains(type)) {
				throw new EventPayloadRegistryBuilderException(DUPLICATE_TYPE, of("type", type, "interface", eventPayLoadInterface));
			} else {
				allTypes.add(type);
			}
			
			this.payloadType = type;
			this.payloadVersion = cqrsEventPayload.version();
			return this;
		}

		public EventPayloadDefinition<T> build() {
			return new EventPayloadDefinition<>(payloadType, payloadVersion, serializer, deserializer);
		}
	}

}