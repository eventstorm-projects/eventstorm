package eu.eventstorm.core.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

class AggregateIdGeneratorManagerTest {

	@Test
	void test() {
		ImmutableMap.Builder<String, AggregateIdGenerator> builder = ImmutableMap.builder();
		builder.put("id01", AggregateIdGeneratorFactory.inMemoryInteger());
		builder.put("id02", AggregateIdGeneratorFactory.inMemoryLong());
		AggregateIdGeneratorManager manager = new AggregateIdGeneratorManager(builder.build());
		
		assertEquals("1", manager.getAggregateIdGenerator("id01").generate().toStringValue());
		assertEquals("2", manager.getAggregateIdGenerator("id01").generate().toStringValue());
		assertEquals("1", manager.getAggregateIdGenerator("id02").generate().toStringValue());
		assertEquals("2", manager.getAggregateIdGenerator("id02").generate().toStringValue());
	}
}
