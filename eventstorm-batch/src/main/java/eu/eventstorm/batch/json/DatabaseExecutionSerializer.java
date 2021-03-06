package eu.eventstorm.batch.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import eu.eventstorm.batch.db.DatabaseExecution;
import eu.eventstorm.util.Dates;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DatabaseExecutionSerializer extends StdSerializer<DatabaseExecution> {

	DatabaseExecutionSerializer() {
		super(DatabaseExecution.class, false);
	}

	@Override
	public void serialize(DatabaseExecution value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("uuid", value.getUuid());
		gen.writeStringField("name", value.getName());
		gen.writeFieldName("event");
		gen.writeRawValue(value.getEvent());
		gen.writeStringField("status", value.getStatus());
		writeTimestamp(gen, "createdAt", value.getCreatedAt());
		gen.writeStringField("createdBy", value.getCreatedBy());
		writeTimestamp(gen, "startedAt", value.getStartedAt());
		writeTimestamp(gen, "endedAt", value.getEndedAt());
		gen.writeEndObject();
	}

	private static void writeTimestamp(JsonGenerator gen, String field, Timestamp timestamp) throws IOException {
		if (timestamp == null) {
			gen.writeNullField(field);
		} else {
			gen.writeStringField(field, Dates.format(timestamp.toLocalDateTime()));
		}
	}
}
