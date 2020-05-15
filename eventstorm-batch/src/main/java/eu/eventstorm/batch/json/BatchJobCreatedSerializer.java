package eu.eventstorm.batch.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.protobuf.util.JsonFormat;

import eu.eventstorm.cqrs.batch.BatchJobCreated;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SuppressWarnings({ "serial" })
final class BatchJobCreatedSerializer extends StdSerializer<BatchJobCreated> {

	private final JsonFormat.Printer printer;
	
	BatchJobCreatedSerializer() {
		super(BatchJobCreated.class, false);
		this.printer = JsonFormat.printer().omittingInsignificantWhitespace();
	}

	@Override
	public void serialize(BatchJobCreated value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeRaw(printer.print(value));
	}

}
