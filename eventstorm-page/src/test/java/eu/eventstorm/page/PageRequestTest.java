package eu.eventstorm.page;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;


class PageRequestTest {

    @Test
    void testBuilder() throws JSONException {
        PageRequestBuilder builder = PageRequest.of("range=0-9", 0, 10);
        PageRequest pr = builder.build();
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"sorts\":[]}", pr.toString(), false);

        builder.withFilter(Filters.newProperty("prop1", Operator.EQUALS, "value1", null));
        pr = builder.build();
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":{\"property\":\"prop1\",\"operator\":\"EQUALS\",\"raw\":\"value1\"},\"sorts\":[]}", pr.toString(), false);

        builder.withSort(Sort.asc("titi"));
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":{\"property\":\"prop1\",\"operator\":\"EQUALS\",\"raw\":\"value1\"},\"sorts\":[{\"property\":\"titi\",\"isAscending\":\"true\"}]}", pr.toString(), false);
    }

}
