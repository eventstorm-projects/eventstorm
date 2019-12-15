package eu.eventstorm.sql.impl;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.TransactionManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseBuilder {

	private final Dialect.Name dialect;
	private TransactionManager transactionManager;
	private JsonMapper jsonMapper;
	private String defaultSchema;
	private DatabaseExternalDefintion databaseExternalDefintion;
	private ImmutableList.Builder<Module> modules = ImmutableList.builder();

	private DatabaseBuilder(Dialect.Name dialect) {
		this.dialect = dialect;
	}

	public static DatabaseBuilder from(Dialect.Name dialect) {
		return new DatabaseBuilder(dialect);
	}

	public DatabaseBuilder withTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
		return this;
	}

	public DatabaseBuilder withJsonMapper(JsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
		return this;
	}

	public DatabaseBuilder withDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
		return this;
	}

	public DatabaseBuilder withDatabaseExternalDefintion(DatabaseExternalDefintion databaseExternalDefintion) {
		this.databaseExternalDefintion = databaseExternalDefintion;
		return this;
	}

	public DatabaseBuilder withModule(Module module) {
		this.modules.add(module);
		return this;
	}

	public Database build() {
		return new DatabaseImpl(dialect, transactionManager, jsonMapper, defaultSchema, databaseExternalDefintion, modules.build());
	}
	
}
