package eu.eventstorm.sql.dialect;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.util.Strings;

abstract class AbstractDialect implements Dialect {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDialect.class);

	private final Database database;

	public AbstractDialect(Database database) {
		this.database = database;
	}

	final String prefix(SqlTable table) {
		Module module = this.database.getModule(table);
		if (module == null) {
			throw new EventstormDialectException(EventstormDialectException.Type.MODULE_NOT_FOUND, ImmutableMap.of("table", table));
		}
		requireNonNull(this.database.getModule(table), "table [" + table);
		if (Strings.isEmpty(module.catalog())) {
			return table.name();
		} else {
			return module.catalog().concat(".").concat(table.name());
		}
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
			LOGGER.warn("Failed to wrap({},{},{}) -> [{}]", appendable, table, alias, cause);
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
		} catch (IOException cause) {
			LOGGER.warn("Failed to wrap({},{},{}) -> [{}]", appendable, column, alias, cause);
		}
	}

	protected abstract String aliasSeparator();
}
