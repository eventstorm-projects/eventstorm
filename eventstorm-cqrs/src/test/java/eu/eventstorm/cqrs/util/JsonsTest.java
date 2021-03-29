package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class JsonsTest {

    @Test
    void testSkip() throws IOException {

      String value =  "{\n" +
                "    \"fruit\": \"Apple\",\n" +
                "    \"size\": \"Large\",\n" +
                "    \"color\": \"Red\"\n" +
                "}";

        JsonFactory factory = new JsonFactory();
        JsonParser  parser  = factory.createParser(value);

        parser.nextToken();
        parser.nextToken();

        Assertions.assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
        Assertions.assertEquals("fruit", parser.currentName());

        Jsons.ignoreField(parser);
        parser.nextToken();
        Assertions.assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
        Assertions.assertEquals("size", parser.currentName());

        System.out.println("jsonToken = " + parser.currentToken());
        System.out.println("jsonToken = " + parser.currentName());



    }

}
