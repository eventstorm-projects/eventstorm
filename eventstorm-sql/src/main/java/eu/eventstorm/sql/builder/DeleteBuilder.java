package eu.eventstorm.sql.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.Expression;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DeleteBuilder extends AbstractBuilder {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteBuilder.class);

    private final Database database;
    private final SqlTable table;
    private Expression where;

    public DeleteBuilder(Database database, SqlTable table) {
        super(database);
        this.database = database;
        this.table = table;
    }

    public DeleteBuilder where(Expression expression) {
        this.where = expression;
        return this;
    }

    public String build() {
        String sql = "DELETE FROM " +
                table(this.table, false) +
                " WHERE " +
                where.build(database.dialect(), false);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SQL [{}]", sql);
        }

        return sql;
    }


}