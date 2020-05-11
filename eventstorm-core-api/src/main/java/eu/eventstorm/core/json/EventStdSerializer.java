package eu.eventstorm.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;

import eu.eventstorm.core.Event;

@SuppressWarnings("serial")
public final class EventStdSerializer extends StdSerializer<Event> {

	private final Printer printer;
	
	EventStdSerializer(TypeRegistry typeRegistry) {
		super(Event.class, false);
		this.printer  = JsonFormat.printer().usingTypeRegistry(typeRegistry).omittingInsignificantWhitespace();
	}

	@Override
	public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		 gen.writeStartObject();
		 gen.writeFieldName("streamId");
		 gen.writeString(value.getStreamId());
		 gen.writeFieldName("stream");
		 gen.writeString(value.getStream());
		 gen.writeFieldName("timestamp");
		 gen.writeString(value.getTimestamp());
		 gen.writeFieldName("revision");
		 gen.writeNumber(value.getRevision());
		
		
		 gen.writeFieldName("data");
		 gen.writeRaw(':');
		 gen.writeRaw(printer.print(value.getData()));
		 
		 gen.writeEndObject(); 
		 
		
		
	}
	
}
