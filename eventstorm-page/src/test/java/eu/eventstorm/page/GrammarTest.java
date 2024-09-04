package eu.eventstorm.page;


import eu.eventstorm.page.parser.PageableLexer;
import eu.eventstorm.page.parser.PageableParser;
import eu.eventstorm.page.parser.PageableParser.FilterItemContext;
import eu.eventstorm.page.parser.PageableParser.RequestContext;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static eu.eventstorm.page.PageRequests.unwrap;
import static org.antlr.v4.runtime.CharStreams.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LoggerInstancePostProcessor.class)
class GrammarTest {

	@Test
	void test() {
		
		PageableLexer lexer = new PageableLexer(fromString("range=0-49&filter=type[eq]'value',type2[eq]'value2'&sort=+code,-toto,+titi"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);
		
		RequestContext ctx =parser.request();
		
		System.out.println(ctx);

		assertEquals("range=0-49&filter=type[eq]'value',type2[eq]'value2'&sort=+code,-toto,+titi", ctx.getText());
		assertEquals("range=0-49", ctx.range().getText());
		assertEquals("filter=type[eq]'value',type2[eq]'value2'", ctx.filter().getText());
		assertEquals("sort=+code,-toto,+titi", ctx.sort().getText());

		//filter
		assertEquals("type[eq]'value',type2[eq]'value2'", ctx.filter().filterExpression().getText());
		//assertEquals(2, ctx.filter().filterContent().filterContent().get(0).getText());
		assertEquals("type[eq]'value'", ctx.filter().filterExpression().filterItems().filterItem(0).getText());
		assertEquals("type2[eq]'value2'", ctx.filter().filterExpression().filterItems().filterItem(1).getText());

		//sort
		assertEquals("+code,-toto,+titi", ctx.sort().sortContent().getText());
		assertEquals(3, ctx.sort().sortContent().sortItem().size());
		assertEquals("+code", ctx.sort().sortContent().sortItem(0).getText());
		assertEquals("-toto", ctx.sort().sortContent().sortItem(1).getText());
		assertEquals("+titi", ctx.sort().sortContent().sortItem(2).getText());

		lexer = new PageableLexer(fromString("range=0-9&sort=-code,+titi"));
		tokens = new CommonTokenStream(lexer);
		parser = new PageableParser(tokens);
		ctx = parser.request();

		assertEquals("range=0-9", ctx.range().getText());
		assertEquals("sort=-code,+titi", ctx.sort().getText());

	}
	
	@Test
	void testRange() {
		
		PageableLexer lexer = new PageableLexer(fromString("range=0-49"));
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

		PageableLexer lexer = new PageableLexer(fromString("filter=col[in][1;2;3]"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx = parser.request();

		assertEquals(1, ctx.filter().filterExpression().filterItems().filterItem().size());
		PageableParser.FilterItemContext fic = ctx.filter().filterExpression().filterItems().filterItem(0);

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

		PageableLexer lexer = new PageableLexer(fromString("filter=prop01[in]['hello';'world';'12345';'hello world']"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx = parser.request();

		assertEquals(1, ctx.filter().filterExpression().filterItems().filterItem().size());
		FilterItemContext fic = ctx.filter().filterExpression().filterItems().filterItem(0);

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

	@Test
	void testEscapeQuote() {

		PageableLexer lexer = new PageableLexer(fromString("range=0-49&filter=type[cnt]'val''ue'"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx =parser.request();

		assertEquals("range=0-49&filter=type[cnt]'val''ue'", ctx.getText());
		assertEquals("range=0-49", ctx.range().getText());
		assertEquals("filter=type[cnt]'val''ue'", ctx.filter().getText());

		//filter
		assertEquals("type[cnt]'val''ue'", ctx.filter().filterExpression().getText());
		assertEquals(1, ctx.filter().filterExpression().filterItems().filterItem().size());
		assertEquals("type[cnt]'val''ue'", ctx.filter().filterExpression().filterItems().filterItem(0).getText());

		assertEquals("type", ctx.filter().filterExpression().filterItems().filterItem(0).property().getText());
		assertEquals("[cnt]", ctx.filter().filterExpression().filterItems().filterItem(0).op().getText());
		assertEquals("'val''ue'", ctx.filter().filterExpression().filterItems().filterItem(0).value().singleValue().STRING().getText());
		assertEquals("'val'ue'", ctx.filter().filterExpression().filterItems().filterItem(0).value().singleValue().STRING().getText().replaceAll("''","'"));

		lexer = new PageableLexer(fromString("range=0-9&sort=-code,+titi"));
		tokens = new CommonTokenStream(lexer);
		parser = new PageableParser(tokens);
		ctx = parser.request();

		assertEquals("range=0-9", ctx.range().getText());
		assertEquals("sort=-code,+titi", ctx.sort().getText());

	}

	@Test
	void testOr() {

		PageableLexer lexer = new PageableLexer(fromString(
				"filter=(type[eq]'value')or(type2[eq]'value2')"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);

		RequestContext ctx =parser.request();

		System.out.println(ctx);

		assertEquals("filter=(type[eq]'value')or(type2[eq]'value2')", ctx.filter().getText());

		//filter
		assertEquals("(type[eq]'value')or(type2[eq]'value2')", ctx.filter().filterExpression().getText());
		//assertEquals(2, ctx.filter().filterContent().filterContent().get(0).getText());
		assertEquals("type[eq]'value'", ctx.filter().filterExpression().filterExpressionOr().filterExpressionLeft().getText());
		assertEquals("type2[eq]'value2'", ctx.filter().filterExpression().filterExpressionOr().filterExpressionRight().getText());

	}

}

