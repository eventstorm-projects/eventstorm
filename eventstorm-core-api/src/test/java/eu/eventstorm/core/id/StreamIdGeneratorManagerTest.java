package eu.eventstorm.core.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

class StreamIdGeneratorManagerTest {

	@Test
	void test() {
		ImmutableMap.Builder<String, StreamIdGenerator> builder = ImmutableMap.builder();
		builder.put("id01", StreamIdGeneratorFactory.inMemoryInteger());
		builder.put("id02", StreamIdGeneratorFactory.inMemoryLong());
		StreamIdGeneratorManager manager = new StreamIdGeneratorManager(builder.build());
		
		assertEquals("1", manager.getAggregateIdGenerator("id01").generate().toStringValue());
		assertEquals("2", manager.getAggregateIdGenerator("id01").generate().toStringValue());
		assertEquals("1", manager.getAggregateIdGenerator("id02").generate().toStringValue());
		assertEquals("2", manager.getAggregateIdGenerator("id02").generate().toStringValue());
	}
}
