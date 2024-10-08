package eu.eventstorm.page;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.page.parser.PageableLexer;
import eu.eventstorm.page.parser.PageableParser;
import eu.eventstorm.page.parser.PageableParser.FilterContext;
import eu.eventstorm.page.parser.PageableParser.FilterExpressionAndContext;
import eu.eventstorm.page.parser.PageableParser.FilterExpressionContext;
import eu.eventstorm.page.parser.PageableParser.FilterExpressionOrContext;
import eu.eventstorm.page.parser.PageableParser.FilterItemContext;
import eu.eventstorm.page.parser.PageableParser.RangeContext;
import eu.eventstorm.page.parser.PageableParser.RequestContext;
import eu.eventstorm.page.parser.PageableParser.SingleValueContext;
import eu.eventstorm.page.parser.PageableParser.SortContext;
import eu.eventstorm.page.parser.PageableParser.SortItemContext;
import eu.eventstorm.page.parser.PageableParser.ValueContext;
import eu.eventstorm.util.Strings;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.antlr.v4.runtime.CharStreams.fromString;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class PageRequests {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageRequests.class);

    private static final BaseErrorListener BASE_ERROR_LISTENER = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException cause) {
            throw new PageRequestException(PageRequestException.Type.PARSING, ImmutableMap.of("input", msg, "offset", charPositionInLine), cause);
        }
    };


    private PageRequests() {
    }

    public static PageRequest parse(String query, EvaluatorDefinition evaluatorDefinition) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("parseQuery [{}]", query);
        }

        if (Strings.isEmpty(query)) {
            throw new PageRequestException(PageRequestException.Type.EMPTY, ImmutableMap.of());
        }

        RequestContext ctx = parse(query);

        PageRequestBuilder builder = parseRange(query, ctx.range());
        parseFilter(builder, ctx.filter(), evaluatorDefinition);
        parseSort(builder, ctx.sort());

        builder.withEvaluator(evaluatorDefinition);

        return builder.build();
    }

    public static String unwrap(String value) {
        return value.substring(1, value.length() - 1).replaceAll("''", "'");
    }

    private static RequestContext parse(String query) {
        PageableParser parser = new PageableParser(new CommonTokenStream(new PageableLexer(fromString(query))));
        parser.removeErrorListeners();
        parser.addErrorListener(BASE_ERROR_LISTENER);
        return parser.request();
    }

    private static void parseSort(PageRequestBuilder builder, SortContext sortContext) {
        if (sortContext == null) {
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
                builder.withSort(Sort.asc(sic.getText().substring(1)));
            } else {
                builder.withSort(Sort.desc(sic.getText().substring(1)));
            }
        }
    }

    private static void parseFilter(PageRequestBuilder builder, FilterContext filterContext, EvaluatorDefinition evaluatorDefinition) {
        if (filterContext != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("found filter() -> [{}]", filterContext.getText());
            }
            builder.withFilter(parseFilter(filterContext.filterExpression(), evaluatorDefinition));
        }
    }

    private static Filter parseFilter(FilterExpressionContext expressionContext, EvaluatorDefinition evaluatorDefinition) {
        if (expressionContext.filterExpressionAnd() != null) {
            FilterExpressionAndContext andContext = expressionContext.filterExpressionAnd();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("found filter and({},{}})", andContext.filterExpressionLeft().getText(), andContext.filterExpressionRight().getText());
            }
            return Filters.newAnd(
                    parseFilter(andContext.filterExpressionLeft().filterExpression(), evaluatorDefinition),
                    parseFilter(andContext.filterExpressionRight().filterExpression(),evaluatorDefinition));
        }

        if (expressionContext.filterExpressionOr() != null) {
            FilterExpressionOrContext andContext = expressionContext.filterExpressionOr();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("found filter or({},{}})", andContext.filterExpressionLeft().getText(), andContext.filterExpressionRight().getText());
            }
            return Filters.newOr(
                    parseFilter(andContext.filterExpressionLeft().filterExpression(), evaluatorDefinition),
                    parseFilter(andContext.filterExpressionRight().filterExpression(), evaluatorDefinition));
        }

        if (expressionContext.filterItems() != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("found filters ({})", expressionContext.filterItems().getText());
            }

            Filter[] builders = new Filter[expressionContext.filterItems().filterItem().size()];
            for (int i = 0; i < expressionContext.filterItems().filterItem().size(); i++) {
                FilterItemContext fic = expressionContext.filterItems().filterItem(i);
                Operator operator = Operator.from(fic.op().getText());
                builders[i] = Filters.newProperty(fic.property().getText(), operator, fic.value().getText(), evaluatorDefinition.enrich(operator, getValues(fic.value())));
            }

            if (builders.length == 1) {
                return builders[0];
            }

            return Filters.newAnd(builders);
        }

        throw new IllegalStateException();
    }

    private static PageRequestBuilder parseRange(String query, RangeContext rangeContext) {

        int rangeStart = 0;
        int rangeEnd = 24;

        if (rangeContext != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("found range() -> [{}]", rangeContext.getText());
            }
            rangeStart = Integer.parseInt(rangeContext.rangeContent().rangeStart().integer().getText());
            rangeEnd = Integer.parseInt(rangeContext.rangeContent().rangeEnd().integer().getText());
        }

        return PageRequest.of(query, rangeStart, rangeEnd - rangeStart + 1);
    }

    private static List<String> getValues(ValueContext vc) {
        if (vc.singleValue() != null) {
            return of(value(vc.singleValue()));
        } else {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            for (SingleValueContext svc : vc.multipleValue().singleValue()) {
                builder.add(value(svc));
            }
            return builder.build();
        }
    }

    private static String value(SingleValueContext svc) {
        if (svc != null) {
            if (svc.integer() != null) {
                return svc.integer().getText();
            } else {
                return PageRequests.unwrap(svc.STRING().getText());
            }
        }
        return Strings.EMPTY;
    }

	/*
	private interface ExpressionFunction {

		Expression apply(SqlColumn column, FilterItemContext fic);
		
		PreparedStatementIndexSetter getPreparedStatementIndexSetter(SqlQueryDescriptor queryDescriptor, String property, FilterItemContext fic);

	}
	
	private static final class SimpleExpressionFunction implements ExpressionFunction {
		private final Function<SqlColumn, Expression> func;
		public SimpleExpressionFunction(Function<SqlColumn, Expression> func) {
			this.func = func;
		}
		public Expression apply(SqlColumn column, FilterItemContext fic) {
			return func.apply(column);
		}
		public PreparedStatementIndexSetter getPreparedStatementIndexSetter(SqlQueryDescriptor queryDescriptor, String property, FilterItemContext fic) {
			return queryDescriptor.getPreparedStatementIndexSetter(property, value(fic));
		}
	}
	
	private static final class ContainsExpressionFunction implements ExpressionFunction {
		public Expression apply(SqlColumn column, FilterItemContext fic) {
			return Expressions.like(column);
		}
		public PreparedStatementIndexSetter getPreparedStatementIndexSetter(SqlQueryDescriptor queryDescriptor, String property, FilterItemContext fic) {
			return queryDescriptor.getPreparedStatementIndexSetter(property, "%" + value(fic) + "%");
		}
	}
	
	private static final class InExpressionFunction implements ExpressionFunction {
		public Expression apply(SqlColumn column, FilterItemContext fic) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("InExpressionFunction.apply({})", fic.value().getText());
			}
			if (fic.value().singleValue() != null) {
				return Expressions.in(column, 1);
			} else {
				return Expressions.in(column, fic.value().multipleValue().singleValue().size());	
			}
			
		}
		public PreparedStatementIndexSetter getPreparedStatementIndexSetter(SqlQueryDescriptor queryDescriptor, String property, FilterItemContext fic) {
			ImmutableList.Builder<PreparedStatementIndexSetter> builder = ImmutableList.builder();
			for (SingleValueContext svc : fic.value().multipleValue().singleValue()) {
				builder.add(queryDescriptor.getPreparedStatementIndexSetter(property, value(svc)));	
			}
			ImmutableList<PreparedStatementIndexSetter> list = builder.build();
			return (ps, index) -> {
				for (int i = 0 ; i < list.size(); i++) {
					list.get(i).set(ps, index + i);
				}
				return index + list.size();
			};
		}
	}
	
	private static String value(FilterItemContext fic) {
		return value(fic.value().singleValue());
	}
	
	private static String value(SingleValueContext svc) {
		if (svc != null) {
			if (svc.integer() != null) {
				return svc.integer().getText();
			} else {
				return PageRequests.unwrap(svc.STRING().getText());
			}
		}
		return Strings.EMPTY;
	}
	
	private static List<String> getRawValues(ValueContext vc) {
		if (vc.singleValue() != null) {
			return of(value(vc.singleValue()));
		} else {
			ImmutableList.Builder<String> builder = ImmutableList.builder();
			for (SingleValueContext svc : vc.multipleValue().singleValue()) {
				builder.add(value(svc));
			}
			return builder.build();
		}
	}
	*/
}
