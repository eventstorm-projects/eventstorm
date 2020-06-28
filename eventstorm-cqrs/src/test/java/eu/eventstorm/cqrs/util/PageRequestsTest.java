package eu.eventstorm.cqrs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import eu.eventstorm.cqrs.SqlQueryDescriptor;
import eu.eventstorm.sql.builder.OrderType;
import eu.eventstorm.sql.page.PageRequest;

class PageRequestsTest {

	@Test
	void testParseOnlyRange() {
		PageRequest pr = PageRequests.parse("range=0-9", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(0, pr.getFilters().size());
		assertEquals(0, pr.getOrders().size());
	}
	
	@Test
	void testParseRangeAndFilter() {
		PageRequest pr = PageRequests.parse("range=0-9&filter=code[eq]ab", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(0, pr.getOrders().size());
		assertEquals(1, pr.getFilters().toExpressions().size());
		
		pr = PageRequests.parse("range=0-19&filter=code[eq]25", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(20, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(0, pr.getOrders().size());
		assertEquals(1, pr.getFilters().toExpressions().size());
		
		pr = PageRequests.parse("range=0-19&filter=code[eq]25,type[ge]ab", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(20, pr.getSize());
		assertEquals(2, pr.getFilters().size());
		assertEquals(0, pr.getOrders().size());
		assertEquals(2, pr.getFilters().toExpressions().size());
	}
	
	@Test
	void testParseRangeAndFilterAndSort() {
		PageRequest pr = PageRequests.parse("range=0-9&filter=code[eq]ab&sort=+code", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(1, pr.getOrders().size());
		assertEquals(OrderType.ASC, pr.getOrders().get(0).type());
		
		pr = PageRequests.parse("range=0-9&filter=code[eq]ab&sort=+code,-type", Mockito.mock(SqlQueryDescriptor.class));
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(2, pr.getOrders().size());
		assertEquals(OrderType.ASC, pr.getOrders().get(0).type());
		assertEquals(OrderType.DESC, pr.getOrders().get(1).type());
	}
	
	@Test
	void testInvalid() {
		PageRequestException pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("range=012&filter=", Mockito.mock(SqlQueryDescriptor.class)));
		assertEquals("no viable alternative at input 'range=012&'", pre.getValues().get("input"));
		assertEquals(9, pre.getValues().get("offset"));
		
		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("range=0-12&filter=", Mockito.mock(SqlQueryDescriptor.class)));
		assertEquals("no viable alternative at input 'range=0-12&filter='", pre.getValues().get("input"));
		assertEquals(18, pre.getValues().get("offset"));
		
		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("filter=code[eq]25&range=0-12", Mockito.mock(SqlQueryDescriptor.class)));
		assertEquals(18, pre.getValues().get("offset"));
	}
}
