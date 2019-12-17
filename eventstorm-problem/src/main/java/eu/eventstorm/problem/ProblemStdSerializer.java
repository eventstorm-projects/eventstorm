package eu.eventstorm.problem;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map.Entry;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.eventstorm.util.Strings;

@SuppressWarnings("serial")
final class ProblemStdSerializer extends StdSerializer<Problem> {

	public static final ProblemStdSerializer INSTANCE = new ProblemStdSerializer();

	private ProblemStdSerializer() {
		super(Problem.class);
	}

	@Override
	public void serialize(Problem value, JsonGenerator gen, SerializerProvider provider) throws IOException {

		gen.writeStartObject();

		gen.writeFieldName("trace_id");
		gen.writeString(value.getTraceId());
		
		gen.writeFieldName("timestamp");
		gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value.getTimestamp()));
		
		gen.writeFieldName("type");
		gen.writeString(value.getType().toASCIIString());
		
		gen.writeFieldName("title");
		gen.writeString(value.getTitle());
		
		if (!Strings.isEmpty(value.getDetail())) {
			gen.writeFieldName("detail");
			gen.writeString(value.getDetail());	
		}
		
		if (value.getInstance() != null) {
			gen.writeFieldName("instance");
			gen.writeString(value.getInstance().toASCIIString());
		}
		
		// write status
		gen.writeFieldName("status");
		gen.writeNumber(value.getStatus());
		
	
		for (Entry<String,Object> entry : value.getParams().entrySet()) {
			gen.writeFieldName(entry.getKey());
			if (entry.getValue() instanceof String) {
				gen.writeString((String)entry.getValue());	
			} else if (entry.getValue() instanceof Optional) {
				Optional<?> optional = (Optional<?>) entry.getValue();
				if (optional.isPresent()) {
					gen.writeObject(optional.get());
				} else {
					gen.writeNull();
				}
			} else {
				gen.writeObject(entry.getValue());
			}
		}; 

		gen.writeEndObject();

	}

}
