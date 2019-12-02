package eu.eventstorm.core.json.jackson;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class EventSerializer extends StdSerializer<Event> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));

	EventSerializer() {
		super(Event.class, false);
	}

	@Override
	public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeStartObject();

		gen.writeFieldName("specversion");
		gen.writeString("1.0");

		gen.writeFieldName("type");
		gen.writeString(value.type());

		gen.writeFieldName("time");
		gen.writeString(FORMATTER.format(value.time()));

		gen.writeFieldName("subject");
		gen.writeString(value.subject());

		gen.writeFieldName("id");
		gen.writeString(value.subject());

		gen.writeFieldName("datacontenttype");
		gen.writeObject("application/json");

		gen.writeFieldName("data");
		gen.writeObject(value.data());

		gen.writeEndObject();
	}

}
