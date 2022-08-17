package eu.eventstorm.sql.builder;

import eu.eventstorm.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class SelectBuilderFromSubSelect extends AbstractBuilder {

	/**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SelectBuilderFromSubSelect.class);
    
	private final SubSelect subSelect;
	private Expression where;

	public SelectBuilderFromSubSelect(Database database, SubSelect subSelect) {
		super(database);
		this.subSelect = subSelect;
	}

	public SqlQuery build() {

		StringBuilder builder = new StringBuilder(2048);

		builder.append("SELECT * FROM (").append(subSelect.sql()).append(')');
		if (!Strings.isEmpty(subSelect.alias())) {
			builder.append(' ').append(subSelect.alias());
		}
		if (where != null) {
			builder.append(" WHERE ");
			builder.append(where.build(database().dialect(), true));
		}

		String sql = builder.toString();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SQL [{}]", sql);
		}

		return new SqlQueryImpl(sql);

	}

	public SelectBuilderFromSubSelect where(Expression expression) {
		this.where = expression;
		return this;
	}

}
