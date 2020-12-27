package eu.eventstorm.sql.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.json.JacksonJsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseBuilder {

	private final Dialect.Name dialect;
	private TransactionManager transactionManager;
	private JsonMapper jsonMapper;
	private final ImmutableList.Builder<Module> modules = ImmutableList.builder();
	private final ImmutableList.Builder<DatabaseExternalConfig> externals = ImmutableList.builder();

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

	public DatabaseBuilder withModule(Module module) {
		this.modules.add(module);
		return this;
	}
	
	public DatabaseModuleBuilder withModuleAndExternalConfig(Module module) {
		this.modules.add(module);
		return new DatabaseModuleBuilder(module);
	}
	
	public class DatabaseModuleBuilder {
		private final Module module;
        private final List<SqlSequence> sequences;
        
        private DatabaseModuleBuilder(Module module) {
        	this.module = module;
            this.sequences = new ArrayList<>();
        }
        public DatabaseModuleBuilder withSequence(SqlSequence sequence) {
            this.sequences.add(sequence);
            return this;
        }
        public DatabaseBuilder and() {
        	externals.add(consumer -> {
        			for (SqlSequence sequence : sequences) {
        				consumer.accept(module, sequence);
        			}
			});
            return DatabaseBuilder.this;
        }
     }

	public Database build() {
	    if (jsonMapper == null) {
	        jsonMapper = new JacksonJsonMapper();
	    }
		return new DatabaseImpl(dialect, transactionManager, jsonMapper, modules.build(), externals.build());
	}
	
}
