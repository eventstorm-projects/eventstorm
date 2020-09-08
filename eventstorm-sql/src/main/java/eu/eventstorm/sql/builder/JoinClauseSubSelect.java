package eu.eventstorm.sql.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class JoinClauseSubSelect extends AbstractBuilder implements JoinClause {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinClauseSubSelect.class);

    private final JoinType type;
    private final SubSelect subSelect;
    private final SqlColumn targetColumn;
    private final SqlColumn column;

    public JoinClauseSubSelect(Database database, JoinType type, SubSelect subSelect, SqlColumn targetColumn, SqlTable from, SqlColumn column) {
        super(database);
        this.type = type;
        this.subSelect = subSelect;
        this.targetColumn = targetColumn.fromTable(subSelect.table());
        this.column = column.fromTable(from);
    }

    public void build(StringBuilder builder) {

        builder.append(' ');
        builder.append(this.type.name()).append(" JOIN ");
        builder.append('(');
        builder.append(subSelect.sql());
        builder.append(')');
        builder.append(' ');
        builder.append(subSelect.alias());
        builder.append(" ON ");
        database().dialect().wrap(builder, this.targetColumn, true);
        builder.append('=');
        database().dialect().wrap(builder, this.column, true);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Fragment join [{}]", builder);
        }
    }
}