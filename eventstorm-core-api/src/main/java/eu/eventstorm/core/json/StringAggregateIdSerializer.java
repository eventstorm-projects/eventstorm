package eu.eventstorm.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.eventstorm.core.id.StringStreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class StringAggregateIdSerializer extends StdSerializer<StringStreamId> {

	StringAggregateIdSerializer() {
		super(StringStreamId.class, false);
	}

	@Override
	public void serialize(StringStreamId value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeString(value.toStringValue());

	}

}
