package eu.eventsotrm.page;


import eu.eventstorm.page.EvaluatorDefinition;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.page.PageRequestException;
import eu.eventstorm.page.PageRequests;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class PageRequestsTest {

	private EvaluatorDefinition evaluator = mock(EvaluatorDefinition.class);

	@Test
	void testParseOnlyRange() {
		PageRequest pr = PageRequests.parse("range=0-9", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(0, pr.getFilters().size());
		assertEquals(0, pr.getSorts().size());
	}
	
	@Test
	void testParseRangeAndFilter() {


		PageRequest pr = PageRequests.parse("range=0-9&filter=code[eq]'ab'", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(0, pr.getSorts().size());
		assertEquals(1, pr.getFilters().size());
		
		pr = PageRequests.parse("range=0-19&filter=code[eq]25", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(20, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(0, pr.getSorts().size());
		assertEquals(1, pr.getFilters().size());
		
		pr = PageRequests.parse("range=0-19&filter=code[eq]25,type[ge]'ab'", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(20, pr.getSize());
		assertEquals(2, pr.getFilters().size());
		assertEquals(0, pr.getSorts().size());
		assertEquals(2, pr.getFilters().size());

		//PageRequestParserContext mock = context, evaluator;
		//Mockito.when(mock.getPreparedStatementIndexSetter(anyString(), anyString())).thenReturn(context, evaluator);
		
		pr = PageRequests.parse("range=0-19&filter=code[eq]25,type[in]['ab';'cd']", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(20, pr.getSize());
		assertEquals(2, pr.getFilters().size());
		assertEquals(0, pr.getSorts().size());
		assertEquals(2, pr.getFilters().size());
	}
	
	@Test
	void testParseRangeAndFilterAndSort() {
		PageRequest pr = PageRequests.parse("range=0-9&filter=code[eq]'ab'&sort=+code", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(1, pr.getSorts().size());
		assertEquals(true, pr.getSorts().get(0).isAscending());
		
		pr = PageRequests.parse("range=0-9&filter=code[eq]'ab'&sort=+code,-type", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(2, pr.getSorts().size());
		assertEquals(true, pr.getSorts().get(0).isAscending());
		assertEquals(false, pr.getSorts().get(1).isAscending());

		pr = PageRequests.parse("range=0-9&sort=+code,-type&filter=code[eq]'ab'", evaluator);
		assertEquals(0, pr.getOffset());
		assertEquals(10, pr.getSize());
		assertEquals(1, pr.getFilters().size());
		assertEquals(2, pr.getSorts().size());
		assertEquals(true, pr.getSorts().get(0).isAscending());
		assertEquals(false, pr.getSorts().get(1).isAscending());
	}

	@Test
	void testInvalid() {
		PageRequestException pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("range=012&filter=", evaluator));
		assertEquals("no viable alternative at input 'range=012&'", pre.getValues().get("input"));
		assertEquals(9, pre.getValues().get("offset"));

		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("", evaluator));
		assertEquals(PageRequestException.Type.EMPTY, pre.getType());

		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("range=0-12&filter=", evaluator));
		assertEquals("no viable alternative at input 'range=0-12&filter='", pre.getValues().get("input"));
		assertEquals(18, pre.getValues().get("offset"));
		
		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("filter=code[eq]25&range=0-12", evaluator));
		assertEquals(18, pre.getValues().get("offset"));

		pre = assertThrows(PageRequestException.class, () -> PageRequests.parse("filter=code[BAD]25", evaluator));
		assertEquals(11, pre.getValues().get("offset"));
		assertEquals(PageRequestException.Type.PARSING, pre.getType());
	}
}
