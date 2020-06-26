package eu.eventstorm.cqrs.web;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cqrs.QueryException;
import eu.eventstorm.sql.page.Page;

@SuppressWarnings({ "serial", "rawtypes" })
final class PageStdSerializer extends StdSerializer<Page> {

	PageStdSerializer() {
        super(Page.class);
    }

    @Override
    public void serialize(Page payload, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        try (Stream<?> content = payload.getContent()) {
        	content.forEach(t -> {
				try {
					gen.writeObject(t);
				} catch (IOException cause) {
					throw new QueryException(QueryException.Type.FAILED_TO_WRITE_PAGE, ImmutableMap.of("payload",payload));
				}
			});
        }
        gen.writeEndArray();

    }
}