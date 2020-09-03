package eu.eventstorm.cqrs.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.eventstorm.sql.type.Json;

@SuppressWarnings({ "serial"})
final class JsonStdSerializer extends StdSerializer<Json> {

	JsonStdSerializer() {
        super(Json.class);
    }

    @Override
    public void serialize(Json payload, JsonGenerator gen, SerializerProvider provider) throws IOException {
    	byte[] context = payload.write(null);
        gen.writeRawUTF8String(context, 0, context.length);

    }
}