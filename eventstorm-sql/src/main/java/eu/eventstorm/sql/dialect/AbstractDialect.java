package eu.eventstorm.sql.dialect;

import static com.google.common.collect.ImmutableMap.of;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.util.Strings;

abstract class AbstractDialect implements Dialect {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDialect.class);

	private final Database database;

	protected AbstractDialect(Database database) {
		this.database = database;
	}

	final Database getDatabase() {
		return this.database;
	}
	
	final String prefix(SqlTable table) {
		Module module = this.database.getModule(table);
		if (module == null) {
			throw new EventstormDialectException(EventstormDialectException.Type.MODULE_NOT_FOUND, of("table", table));
		}
		return module.getTableName(table);
	}
	
	protected final String prefix(SqlSequence sequence) {
		Module module = this.database.getModule(sequence);
		if (module == null) {
			throw new EventstormDialectException(EventstormDialectException.Type.MODULE_NOT_FOUND, of("sequence", sequence));
		}
		return module.getSequenceName(sequence);
	}
	
	@Override
	public void wrap(Appendable appendable, SqlTable table, boolean alias) {
		try {
			appendable.append(prefix(table));
			if (alias) {
				appendable.append(aliasSeparator());
				appendable.append(table.alias());
			}
		} catch (IOException cause) {
			LOGGER.warn("Failed to wrap({},{},{}) -> {}", appendable, table, alias, cause.getMessage());
		}
	}

	@Override
	public void wrap(Appendable appendable, SqlColumn column, boolean alias) {
		try {
			if (alias) {
				appendable.append(column.table().alias());
				appendable.append('.');
			}

			appendable.append(column.toSql());
			
			if (!Strings.EMPTY.equals(column.alias())) {
				appendable.append(' ');
				appendable.append(column.alias());
			}
			
		} catch (IOException cause) {
			LOGGER.warn("Failed to wrap({},{},{}) -> {}", appendable, column, alias, cause.getMessage());
		}
	}

	protected abstract String aliasSeparator();
}
