package eu.eventstorm.cloudevents;

import eu.eventstorm.core.Event;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloudEventsTest {

    @Test
    void test01() throws JSONException {

        Event event = Event.newBuilder()
                .setStreamId("123456789")
                .setStream("junit-stream")
                .setRevision(1)
                .build();


        CloudEvent ce = CloudEvents.to(event);

        assertEquals("123456789", ce.id());
        assertEquals("junit-stream", ce.type());
        assertEquals("1.0", ce.specVersion());

        JSONAssert.assertEquals("{\"specVersion\":\"1.0\",\"aggregateId\":\"123456789\",\"aggregateType\":\"junit-stream\",\"version\":1}", ce.toString(), false);

    }
}
