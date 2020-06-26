package eu.eventstorm.cqrs.util;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.page.PageRequest;
import eu.eventstorm.sql.page.PageRequestBuilder;

public final class PageRequests {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageRequests.class);
	
	private PageRequests() {
	}
	
	public static RequestContext parse(String query) {
		return new PageableParser(
				new CommonTokenStream(
					new PageableLexer(CharStreams.fromString(query)))).request();
	}
	
	public static PageRequest parseQuery(String query, SqlQueryDescriptor queryDescriptor) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("parseQuery [{}] -> [{}]", query, queryDescriptor);
		}
		
		RequestContext ctx = parse(query); 
		
		PageRequestBuilder builder = parseRange(ctx.range());
		parseFilter(builder, ctx.filter(), queryDescriptor);
		parseSort(builder, ctx.sort(), queryDescriptor);
		
		return builder.build();
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
				String property = fic.key().getText();
				String value = fic.value().getText();
				LOGGER.debug("found filter key () -> [{}]", property );
				LOGGER.debug("found filter op  () -> [{}]", fic.op().getText());
				LOGGER.debug("found filter val () -> [{}]", value);
				builder.withFilter(Expressions.eq(queryDescriptor.get(fic.key().getText())), queryDescriptor.getPreparedStatementIndexSetter(property, value));
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
