package eu.eventstorm.cqrs.util;

import java.util.function.Function;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cqrs.SqlQueryDescriptor;
import eu.eventstorm.cqrs.query.PageableLexer;
import eu.eventstorm.cqrs.query.PageableParser;
import eu.eventstorm.cqrs.query.PageableParser.FilterContext;
import eu.eventstorm.cqrs.query.PageableParser.FilterItemContext;
import eu.eventstorm.cqrs.query.PageableParser.RangeContext;
import eu.eventstorm.cqrs.query.PageableParser.RequestContext;
import eu.eventstorm.cqrs.query.PageableParser.SortContext;
import eu.eventstorm.cqrs.query.PageableParser.SortItemContext;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.page.PageRequest;
import eu.eventstorm.sql.page.PageRequestBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequests {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageRequests.class);
	
	private static final ImmutableMap<String, Function<SqlColumn, Expression>> EXPRESSIONS = ImmutableMap.<String, Function<SqlColumn, Expression>>builder()
			.put("[eq]", Expressions::eq)
			.put("[neq]", Expressions::notEq)
			.put("[ge]", Expressions::ge)
			.put("[gt]", Expressions::gt)
			.put("[le]", Expressions::le)
			.put("[lt]", Expressions::lt)
			.build();
	
	private static final BaseErrorListener BASE_ERROR_LISTENER = new BaseErrorListener() {
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
				int charPositionInLine, String msg, RecognitionException cause) {
			throw new PageRequestException(PageRequestException.Type.PARSING, ImmutableMap.of("input", msg, "offset", charPositionInLine), cause);
		}
	};
	
	
	private PageRequests() {
	}
	
	public static PageRequest parse(String query, SqlQueryDescriptor queryDescriptor) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("parseQuery [{}] -> [{}]", query, queryDescriptor);
		}
		
		RequestContext ctx = parse(query); 
		
		PageRequestBuilder builder = parseRange(ctx.range());
		parseFilter(builder, ctx.filter(), queryDescriptor);
		parseSort(builder, ctx.sort(), queryDescriptor);
		
		return builder.build();
	}

	private static RequestContext parse(String query) {
		PageableParser parser = new PageableParser(
				new CommonTokenStream(
					new PageableLexer(CharStreams.fromString(query))));
		parser.removeErrorListeners();
		parser.addErrorListener(BASE_ERROR_LISTENER);
		return parser.request();
	}
	
	private static void parseSort(PageRequestBuilder builder, SortContext sortContext, SqlQueryDescriptor queryDescriptor) {
		if (sortContext ==  null) {
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("found sort() -> [{}]", sortContext.getText());
		}
		for (SortItemContext sic : sortContext.sortContent().sortItem()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sort on property -> [{}]", sic.getText());
			}
			if (sic.sortAsc() != null) {
				builder.withOrder(Order.asc(queryDescriptor.get(sic.getText().substring(1))));
			} else {
				builder.withOrder(Order.desc(queryDescriptor.get(sic.getText().substring(1))));
			}
		}
	}

	private static void parseFilter(PageRequestBuilder builder, FilterContext filterContext, SqlQueryDescriptor queryDescriptor) {
		if (filterContext != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("found filter() -> [{}]", filterContext.getText());
			}
			for (FilterItemContext fic : filterContext.filterContent().filterItem()) {
				String property = fic.property().getText();
				String op = fic.op().getText();
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("filter for queryDescriptor [{}] ", queryDescriptor);
				}
				
				SqlColumn column = queryDescriptor.get(property);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("filter for property [{}] -> column [{}]", property, column);
				}
				
				Expression expression = EXPRESSIONS.get(op).apply(column);
				builder.withFilter(expression, queryDescriptor.getPreparedStatementIndexSetter(property, fic.value().getText()));
			}
		}
		
	}

	private static PageRequestBuilder parseRange(RangeContext rangeContext) {
		
		int rangeStart = 0;
		int rangeEnd = 24;
		
		if (rangeContext != null) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("found range() -> [{}]", rangeContext.getText());
			}
			rangeStart = Integer.valueOf(rangeContext.rangeContent().rangeStart().integer().getText());
			rangeEnd = Integer.valueOf(rangeContext.rangeContent().rangeEnd().integer().getText());
		}
		
		return PageRequest.of(rangeStart , rangeEnd - rangeStart + 1);
	}
}
