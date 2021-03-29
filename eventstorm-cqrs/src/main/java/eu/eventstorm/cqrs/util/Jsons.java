package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Jsons {

    private Jsons() {
    }

    public static void ignoreField(JsonParser jsonParser) throws IOException {
        JsonToken token = jsonParser.getCurrentToken();
        if (token == JsonToken.START_OBJECT) {
            advanceToClosingToken(jsonParser, JsonToken.START_OBJECT, JsonToken.END_OBJECT);
        } else if (token == JsonToken.START_ARRAY) {
            advanceToClosingToken(jsonParser, JsonToken.START_ARRAY, JsonToken.END_ARRAY);
        } else {
            jsonParser.nextToken();
        }
    }

    private static void advanceToClosingToken(JsonParser jsonParser, JsonToken openingToken, JsonToken closingToken) throws IOException {
        int count = 1;
        do {
            JsonToken currentToken = jsonParser.nextToken();
            if (currentToken == openingToken) {
                count++;
            } else if (currentToken == closingToken) {
                count--;
            }
        } while (count > 0);
    }
}
