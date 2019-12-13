package eu.eventstorm.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

@FunctionalInterface
public interface ParserConsumer<T> {

	void accept(JsonParser parser, T object) throws IOException;
	
}
