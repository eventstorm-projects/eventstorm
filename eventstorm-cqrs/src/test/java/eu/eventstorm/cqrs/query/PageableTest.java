package eu.eventstorm.cqrs.query;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import eu.eventstorm.cqrs.query.PageableParser.RequestContext;

class PageableTest {

	@Test
	void test() {
		
		PageableLexer lexer = new PageableLexer(CharStreams.fromString("range=0-49"));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PageableParser parser = new PageableParser(tokens);
		
		RequestContext ctx =parser.request();
		
		System.out.println(ctx.range().rangeContent().rangeStart().integer().getText());
		System.out.println(ctx.range().rangeContent().rangeEnd().getText());
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
		System.out.println(ctx.sort().sortContent().sortItem(0).STRING());
		
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
}

