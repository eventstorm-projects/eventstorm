package eu.eventstorm.cloudevents.json.jackson;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.eventstorm.cloudevents.CloudEvent;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class CloudEventSerializer extends StdSerializer<CloudEvent> {

	CloudEventSerializer() {
		super(CloudEvent.class, false);
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
		gen.writeObject("application/json");

		gen.writeFieldName("data");
		gen.writeObject(value.data());

		gen.writeEndObject();
	}

}
