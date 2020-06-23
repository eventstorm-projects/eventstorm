package eu.eventstorm.core.page;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import eu.eventstorm.core.page.PageableLexer;
import eu.eventstorm.core.page.PageableParser;
import eu.eventstorm.core.page.PageableParser.RequestContext;

class PageableTest {

	@Test
	void test() {
		
		PageableLexer lexer = new PageableLexer(CharStreams.fromString("range=0-49"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);
		
		RequestContext ctx =parser.request();
		
		System.out.println(ctx.range().rangeStart().integer().getText());
		System.out.println(ctx.range().rangeEnd().getText());
		System.out.println(ctx.filter());
		System.out.println(ctx.sort());
		
		lexer = new PageableLexer(CharStreams.fromString("range=0-49&filter=type[eq]value,type2[eq]value2&sort=+code,-toto,+titi"));
		tokens = new CommonTokenStream(lexer);
		parser = new PageableParser(tokens);
		ctx = parser.request();
		
		System.out.println(ctx);
		System.out.println(ctx.getText());
		System.out.println(ctx.getStart());
		
		
		System.out.println(ctx.range().getText());
		System.out.println(ctx.filter().getText());
		System.out.println(ctx.filter().filterList());
		System.out.println(ctx.filter().filterList().getText());
		System.out.println(ctx.filter().filterList().filterItem().getText());
		System.out.println(ctx.filter().filterList().filterList().getText());
		
		System.out.println(ctx.sort().getText());
		
		System.out.println(ctx.sort().sortList().getText());
		System.out.println(ctx.sort().sortList().sortItem().getText());
		System.out.println(ctx.sort().sortList().sortList().getText());
		
	}
}
