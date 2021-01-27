package eu.eventsotrm.page;

import eu.eventstorm.page.Operator;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.PageRequestBuilder;
import eu.eventstorm.page.Sort;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PageRequestTest {

    @Test
    void testBuilder() throws JSONException {
        PageRequestBuilder builder = PageRequest.of("range=0-9", 0, 10);
        PageRequest pr = builder.build();
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":[],\"sorts\":[]}", pr.toString(), false);

        builder.withFilter("prop1", Operator.EQUALS, "value1");
        pr = builder.build();
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":[{\"property\":\"prop1\",\"operator\":\"EQUALS\",\"raw\":\"value1\"}],\"sorts\":[]}", pr.toString(), false);

        builder.withSort(Sort.asc("titi"));
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":[{\"property\":\"prop1\",\"operator\":\"EQUALS\",\"raw\":\"value1\"}],\"sorts\":[{\"property\":\"titi\",\"isAscending\":\"true\"}]}", pr.toString(), false);
    }

  /*  @Test
    void testFilter() {

        SqlFilterEvaluator evaluator = Mockito.mock(SqlFilterEvaluator.class);

        PageRequestBuilder builder = PageRequest.of(0, 10);
        builder.withFilter("prop1","EQ","value1", evaluator);
        PageRequest pr = builder.build();

        pr.getFilters().forEach(filter -> {
            assertEquals("prop1", filter.getProperty());
            assertEquals("EQ", filter.getOperator());
            assertEquals("value1", filter.getValue());
            assertEquals(evaluator, filter.getEvalutor());
        });

        pr.getFilters().add("prop2","GT","value2", evaluator);

        AtomicInteger integer = new AtomicInteger(0);
        pr.getFilters().forEach(filter -> {
            if  (integer.getAndIncrement() == 0) {
                assertEquals("prop1", filter.getProperty());
                assertEquals("EQ", filter.getOperator());
                assertEquals("value1", filter.getValue());
                assertEquals(evaluator, filter.getEvalutor());
            } else {
                assertEquals("prop2", filter.getProperty());
                assertEquals("GT", filter.getOperator());
                assertEquals("value2", filter.getValue());
                assertEquals(evaluator, filter.getEvalutor());
            }

        });
        assertEquals(2, integer.get());
    }*/

}
