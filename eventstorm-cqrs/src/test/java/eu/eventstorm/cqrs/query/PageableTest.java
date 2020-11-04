package eu.eventstorm.cqrs.query;

import static eu.eventstorm.cqrs.util.PageRequests.unwrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import eu.eventstorm.cqrs.query.PageableParser.FilterItemContext;
import eu.eventstorm.cqrs.query.PageableParser.RequestContext;

class PageableTest {

	@Test
	void test() {
		
		PageableLexer lexer = new PageableLexer(CharStreams.fromString("range=0-49&filter=type[eq]'value',type2[eq]'value2'&sort=+code,-toto,+titi"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);
		
		RequestContext ctx =parser.request();
		
		System.out.println(ctx);
		System.out.println(ctx.getText());
		System.out.println(ctx.getStart());
		
		
		System.out.println(ctx.range().getText());
		System.out.println(ctx.filter().getText());
		System.out.println(ctx.filter().filterContent());
		System.out.println(ctx.filter().filterContent().getText());
		System.out.println(ctx.filter().filterContent().filterItem().size());
		
		System.out.println(ctx.sort().getText());
		
		System.out.println(ctx.sort().sortContent());
		System.out.println(ctx.sort().sortContent().getText());
		System.out.println(ctx.sort().sortContent().sortItem());
		System.out.println(ctx.sort().sortContent().sortItem().size());
		
		System.out.println(ctx.sort().sortContent().sortItem(0));
		System.out.println(ctx.sort().sortContent().sortItem(0).getText());
		System.out.println(ctx.sort().sortContent().sortItem(0).sortAsc());
		System.out.println(ctx.sort().sortContent().sortItem(0).sortDesc());
		System.out.println(ctx.sort().sortContent().sortItem(0).IDENTIFIER());
		
		//System.out.println(ctx.sort().sortList().getText());
		//System.out.println(ctx.sort().sortList().sortItem().getText());
		//System.out.println(ctx.sort().sortList().sortList().getText());
		
		lexer = new PageableLexer(CharStreams.fromString("range=0-9&sort=-code,+titi"));
		tokens = new CommonTokenStream(lexer);
		parser = new PageableParser(tokens);
		ctx = parser.request();
		
		System.out.println(ctx.sort());
		System.out.println(ctx.range());
		System.out.println(ctx.filter());
	}
	
	@Test
	void testRange() {
		
		PageableLexer lexer = new PageableLexer(CharStreams.fromString("range=0-49"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);
		
		RequestContext ctx =parser.request();
		
		assertEquals("range=0-49", ctx.range().getText());
		assertEquals("0-49", ctx.range().rangeContent().getText());
		
		assertEquals("0", ctx.range().rangeContent().rangeStart().integer().getText());
		assertEquals("49", ctx.range().rangeContent().rangeEnd().integer().getText());
		
	}
	
	@Test
	void testArrayValueInteger() {

		PageableLexer lexer = new PageableLexer(CharStreams.fromString("filter=col[in][1;2;3]"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx = parser.request();

		assertEquals(1, ctx.filter().filterContent().filterItem().size());
		FilterItemContext fic = ctx.filter().filterContent().filterItem(0);

		assertEquals("col", fic.property().getText());
		assertEquals("[in]", fic.op().getText());
		assertEquals("[1;2;3]", fic.value().getText());
		assertEquals("[1;2;3]", fic.value().multipleValue().getText());
		assertEquals(3, fic.value().multipleValue().singleValue().size());
		assertEquals("1", fic.value().multipleValue().singleValue().get(0).integer().getText());
		assertEquals("2", fic.value().multipleValue().singleValue().get(1).integer().getText());
	}
	
	@Test
	void testArrayValueString() {

		PageableLexer lexer = new PageableLexer(CharStreams.fromString("filter=prop01[in]['hello';'world';'12345';'hello world']"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx = parser.request();

		assertEquals(1, ctx.filter().filterContent().filterItem().size());
		FilterItemContext fic = ctx.filter().filterContent().filterItem(0);

		assertEquals("prop01", fic.property().getText());
		assertEquals("[in]", fic.op().getText());
		assertEquals("['hello';'world';'12345';'hello world']", fic.value().getText());
		assertEquals("['hello';'world';'12345';'hello world']", fic.value().multipleValue().getText());
		assertEquals(4, fic.value().multipleValue().singleValue().size());
		assertEquals("'hello'", fic.value().multipleValue().singleValue().get(0).STRING().getText());
		assertEquals("'world'", fic.value().multipleValue().singleValue().get(1).STRING().getText());
		assertEquals("'12345'", fic.value().multipleValue().singleValue().get(2).STRING().getText());
		assertEquals("'hello world'", fic.value().multipleValue().singleValue().get(3).STRING().getText());
		
		assertEquals("hello", unwrap(fic.value().multipleValue().singleValue().get(0).STRING().getText()));
		assertEquals("world", unwrap(fic.value().multipleValue().singleValue().get(1).STRING().getText()));
		assertEquals("12345", unwrap(fic.value().multipleValue().singleValue().get(2).STRING().getText()));
		assertEquals("hello world", unwrap(fic.value().multipleValue().singleValue().get(3).STRING().getText()));
	}
	
}

