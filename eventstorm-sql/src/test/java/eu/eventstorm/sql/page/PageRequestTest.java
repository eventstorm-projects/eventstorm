package eu.eventstorm.sql.page;

import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
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
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":{\"filters\":[]},\"orders\":[]}", pr.toString(), false);

        builder.withFilter("prop1","EQ","value1", Mockito.mock(FilterEvaluator.class));
        pr = builder.build();
        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":{\"filters\":[{\"property\":\"prop1\",\"operator\":\"EQ\",\"value\":\"value1\"}]},\"orders\":[]}", pr.toString(), false);

        SqlColumn column = new SqlSingleColumn(new SqlTable("titi", "a"),"toto", true,true, true);
        builder.withOrder(Order.asc(column));

        JSONAssert.assertEquals("{\"query\":\"range=0-9\",\"offset\":0,\"size\":10,\"filter\":{\"filters\":[{\"property\":\"prop1\",\"operator\":\"EQ\",\"value\":\"value1\"}]},\"orders\":[{\"column\":{\"class\":\"SqlSingleColumn\",\"name\":\"toto\",\"table\":{\"class\":\"SqlTable\",\"name\":\"titi\",\"alias\":\"a\"},\"alias\":\"\"},\"type\":\"ASC\"}]}", pr.toString(), false);
    }

    @Test
    void testFilter() {

        FilterEvaluator evaluator = Mockito.mock(FilterEvaluator.class);

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
    }
}
