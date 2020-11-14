package eu.eventstorm.batch.rest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public final class DatabaseResourceQueryStdSerializer extends StdSerializer<DatabaseResourceQuery> {

	public DatabaseResourceQueryStdSerializer() {
		super(DatabaseResourceQuery.class);
	}

	@Override
	public void serialize(DatabaseResourceQuery value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
        gen.writeStringField("id", value.getId());
        gen.writeFieldName("meta");
        gen.writeRawValue(value.getMeta());
        gen.writeStringField("createdBy", value.getCreatedBy());
        gen.writeEndObject();		
	}

}
