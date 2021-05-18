package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> readMapStringString(JsonParser parser) throws IOException {
        com.google.common.collect.ImmutableMap.Builder<String, String> mapBuilder = com.google.common.collect.ImmutableMap.builder();
        // FIELD
        parser.nextToken();
        // START_OBJECT
        parser.nextToken();
        while (parser.currentToken() != JsonToken.END_OBJECT) {
            mapBuilder.put(parser.currentName(), parser.nextTextValue());
            parser.nextToken();
        }
        return mapBuilder.build();
    }

    public static Map<String, ?> readMapStringObject(JsonParser parser) throws IOException {
        com.google.common.collect.ImmutableMap.Builder<String, ? super Object> mapBuilder = com.google.common.collect.ImmutableMap.builder();

        if (JsonToken.START_OBJECT != parser.currentToken()) {
            // FIELD
            parser.nextToken();
            // START_OBJECT
            parser.nextToken();
        }

        while (parser.currentToken() != JsonToken.END_OBJECT) {
            JsonToken token = parser.nextValue();
            if (token == JsonToken.VALUE_STRING) {
                mapBuilder.put(parser.currentName(), parser.getText());
            } else if (token == JsonToken.VALUE_NUMBER_INT) {
                mapBuilder.put(parser.currentName(), parser.getNumberValue());
            } else if (token == JsonToken.START_OBJECT) {
                mapBuilder.put(parser.currentName(), readMapStringObject(parser));
            } else if (token == JsonToken.START_ARRAY) {
                mapBuilder.put(parser.currentName(), readList(parser));
            } else {
                throw new RuntimeException();
            }
            parser.nextToken();
        }
        return mapBuilder.build();
    }

    public static <T> List<T> readList(JsonParser parser,Class<T> clazz) throws IOException {
        com.google.common.collect.ImmutableList.Builder<T> builder = com.google.common.collect.ImmutableList.builder();

        parser.nextToken();

        while (parser.currentToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                throw new IOException("String value not allow");
            } else if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                throw new IOException("number value not allow");
            } else if (parser.currentToken() == JsonToken.START_OBJECT) {
                builder.add(parser.readValueAs(clazz));
            } else {
                throw new IOException("JsonToken [" + parser.currentToken() +"] not allow");
            }
            parser.nextToken();
        }
        return builder.build();
    }

    public static List<String> readListString(JsonParser parser) throws IOException {
        com.google.common.collect.ImmutableList.Builder<String> builder = com.google.common.collect.ImmutableList.builder();
        parser.nextToken();
        while (parser.currentToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                builder.add(parser.getText());
            } else {
                throw new IOException("JsonToken [" + parser.currentToken() +"] not allow");
            }
            parser.nextToken();
        }
        return builder.build();
    }

    public static List<?> readList(JsonParser parser) throws IOException {
        com.google.common.collect.ImmutableList.Builder<? super Object> builder = com.google.common.collect.ImmutableList.builder();

        parser.nextToken();

        while (parser.currentToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                builder.add(parser.getText());
            } else if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                builder.add(parser.getNumberValue());
            } else if (parser.currentToken() == JsonToken.START_OBJECT) {
                builder.add(readMapStringObject(parser));
            } else {
                throw new RuntimeException();
            }

            parser.nextToken();
        }

        return builder.build();
    }
}
