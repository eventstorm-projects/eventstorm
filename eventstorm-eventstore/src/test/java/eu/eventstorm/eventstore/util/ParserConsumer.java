package eu.eventstorm.eventstore.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

@FunctionalInterface
public interface ParserConsumer<T> {

	void accept(JsonParser parser, T object) throws IOException;
	
}
