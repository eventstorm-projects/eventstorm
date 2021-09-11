package eu.eventstorm.cqrs.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.LowerCase;

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

        String value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : \"value3\", \"key4\" : true, \"key5\" : false, \"key6\" : \"value6\" }";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);
        Map<String,String> map = ( Map<String,String>) Jsons.readMapStringObject(parser);

        assertEquals(map.get("key1"), "value1");
        assertEquals(map.get("key2"), "value2");
        assertEquals(map.get("key3"), "value3");
        assertEquals(map.get("key4"), Boolean.TRUE);
        assertEquals(map.get("key5"), Boolean.FALSE);
        assertEquals(map.get("key6"), "value6");

        value = "{ \"key1\" : \"value1\",  \"key2\" : \"value2\", \"key3\" : 123456789}";

        parser = factory.createParser(value);
        Map<String,Object> map2 = ( Map<String,Object>) Jsons.readMapStringObject(parser);

        assertEquals(map2.get("key1"), "value1");
        assertEquals(map2.get("key2"), "value2");
        assertEquals(map2.get("key3"), 123456789);
    }

    @Test
    void testReadStringObjectEmpty() throws IOException {
        String value = "{}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(value);
        Map<String,String> map = ( Map<String,String>) Jsons.readMapStringObject(parser);

        assertEquals(0, map.size());
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

    @Test
    void testReadList() throws IOException{

        String value = "{\"roles\": [{\"id\": \"00001745-05fc-6f45-0000-100000001000\",\"name\": \"Jacques\",\"rank\": 10}]}";

        JsonFactory factory = new ObjectMapper().getFactory();
        JsonParser parser = factory.createParser(value);

        // Start object
        parser.nextToken();
        // String
        parser.nextToken();

        Assertions.assertEquals("roles", parser.currentName());
        List<Info> infos = Jsons.readList(parser, Info.class);
        assertEquals(1, infos.size());
        assertEquals("00001745-05fc-6f45-0000-100000001000", infos.get(0).getId());
        assertEquals("Jacques", infos.get(0).getName());
        assertEquals(10, infos.get(0).getRank());
    }

    private static class Info {
        private String id;
        private String name;
        private int rank;
        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getRank() {
            return rank;
        }
    }

}
