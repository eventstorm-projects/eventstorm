package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonsTest {

    @Test
    void testSkip() throws IOException {

        String value = "{\n" +
                "    \"fruit\": \"Apple\",\n" +
                "    \"size\": \"Large\",\n" +
                "    \"color\": \"Red\"\n" +
                "}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);

        parser.nextToken();
        parser.nextToken();

        assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
        assertEquals("fruit", parser.currentName());

        Jsons.ignoreField(parser);
        parser.nextToken();
        assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
        assertEquals("size", parser.currentName());

    }

    @Test
    void testReadStringString() throws IOException {

        String value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : \"value3\"}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);

        Map<String,String> map = Jsons.readMapStringString(parser);


        assertEquals(map.get("key1"), "value1");
        assertEquals(map.get("key2"), "value2");
        assertEquals(map.get("key3"), "value3");
    }

    @Test
    void testReadStringObjectSimple() throws IOException {

        String value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : \"value3\"}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);
        Map<String,String> map = ( Map<String,String>) Jsons.readMapStringObject(parser);

        assertEquals(map.get("key1"), "value1");
        assertEquals(map.get("key2"), "value2");
        assertEquals(map.get("key3"), "value3");

        value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : 123456789}";

        parser = factory.createParser(value);
        Map<String,Object> map2 = ( Map<String,Object>) Jsons.readMapStringObject(parser);

        assertEquals(map2.get("key1"), "value1");
        assertEquals(map2.get("key2"), "value2");
        assertEquals(map2.get("key3"), 123456789);
    }

    @Test
    void testReadStringObjectArray() throws IOException {

        String value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : 123456, \"key4\" : [\"A\",\"B\",\"C\"]}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);
        Map<String,? super Object> map = ( Map<String,? super Object>) Jsons.readMapStringObject(parser);

        assertEquals(map.get("key1"), "value1");
        assertEquals(map.get("key2"), "value2");
        assertEquals(map.get("key3"), 123456);

        List list = (List) map.get("key4");

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));

        value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : 123456, \"key4\" : [{\"k1\":\"A\"},{\"k2\":\"B_1\",\"k3\":\"B_2\"},{\"k3_1\":\"B_3_1\",\"k3_2\":\"B_3_2\"},{\"k4_1\":\"B_4_1\",\"k4_2\":\"B_4_2\"}]}";

        parser = factory.createParser(value);
        map = ( Map<String,? super Object>) Jsons.readMapStringObject(parser);

        assertEquals(map.get("key1"), "value1");
        assertEquals(map.get("key2"), "value2");
        assertEquals(map.get("key3"), 123456);

        list = (List) map.get("key4");

        assertEquals(4, list.size());

        Map<String,? super Object> tmp = ( Map<String,? super Object>) list.get(0);
        assertEquals("A", tmp.get("k1"));

        tmp = ( Map<String,? super Object>) list.get(1);
        assertEquals("B_1", tmp.get("k2"));
        assertEquals("B_2", tmp.get("k3"));

        tmp = ( Map<String,? super Object>) list.get(2);
        assertEquals("B_3_1", tmp.get("k3_1"));
        assertEquals("B_3_2", tmp.get("k3_2"));

        tmp = ( Map<String,? super Object>) list.get(3);
        assertEquals("B_4_1", tmp.get("k4_1"));
        assertEquals("B_4_2", tmp.get("k4_2"));

    }
}
