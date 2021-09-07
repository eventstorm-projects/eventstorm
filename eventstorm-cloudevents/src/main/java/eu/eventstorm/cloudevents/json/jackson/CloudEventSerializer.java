package eu.eventstorm.cloudevents.json.jackson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.cloudevents.CloudEvent;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class CloudEventSerializer extends StdSerializer<CloudEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventSerializer.class);
	
	private final transient TypeRegistry registry;
	
	private final transient JsonFormat.Printer printer;
	
	private final transient ConcurrentHashMap<String, Parser<DynamicMessage>> descriptors;
	
	CloudEventSerializer(TypeRegistry registry) {
		super(CloudEvent.class, false);
		this.registry = registry;
		this.printer = JsonFormat.printer().usingTypeRegistry(registry).omittingInsignificantWhitespace().includingDefaultValueFields();
		this.descriptors = new ConcurrentHashMap<>();
	}

	@Override
	public void serialize(CloudEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeStartObject();

		gen.writeFieldName("specversion");
		gen.writeString("1.0");

		gen.writeFieldName("type");
		gen.writeString(value.type());

		gen.writeFieldName("time");
		gen.writeString(value.time());

		gen.writeFieldName("subject");
		gen.writeString(value.subject());

		gen.writeFieldName("id");
		gen.writeString(value.id());

		gen.writeFieldName("datacontenttype");
		gen.writeString("application/json");

		gen.writeFieldName("data");

		if (value.data() != null) {
			if (value.data() instanceof Any) {
				gen.writeRaw(":");
				Any any = (Any) value.data();
				Parser<DynamicMessage> parser = this.descriptors.get(any.getTypeUrl());
				if (parser == null) {
					Descriptor descriptor = registry.getDescriptorForTypeUrl(any.getTypeUrl());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("debug [{}] -> [{}] ", any, descriptor);
					}

					parser = DynamicMessage.getDefaultInstance(descriptor).getParserForType();
					this.descriptors.put(any.getTypeUrl(), parser);
				}
				Message message = parser.parseFrom(any.getValue());
				gen.writeRaw(printer.print(message));
			} else if (value.data() instanceof MessageOrBuilder) {
				gen.writeRaw(":");
				gen.writeRaw(printer.print((MessageOrBuilder) value.data()));
			} else {
				gen.writeObject(value.data());
			}
		} else {
			gen.writeRaw(":{}");
		}

		gen.writeEndObject();
	}

}
