package eu.eventstorm.core.json.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

import eu.eventstorm.core.Command;

@FunctionalInterface
public interface ParserConsumer<T extends Command> {

	void accept(JsonParser parser, T command) throws IOException;
	
}
