package eu.eventstorm.sql.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.JsonMapper;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.dialect.Dialects;
import eu.eventstorm.sql.json.JacksonJsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseImpl implements Database {

    /**
     * SLF4J Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseImpl.class);

    /**
     * Dialect for the given datasource.
     */
    private final Dialect dialect;

    private final TransactionManager transactionManager;

    /**
     * Modules supported for this instance of datatabase.
     */
    private final Module[] modules;

    private final ImmutableMap<SqlTable, Module> tables;

    private final ImmutableMap<SqlSequence, Module> sequences;
    
    private final JsonMapper jsonMapper;

    public DatabaseImpl(Dialect.Name dialect, TransactionManager transactionManager, String defaultSchema, Module module, Module... modules) {
    	this(dialect, transactionManager, new JacksonJsonMapper(), defaultSchema, null, module, modules);
    }
    /**
     * @param dataSource    the {@link DataSource}
     * @param dialect
     * @param defaultSchema
     */
    public DatabaseImpl(Dialect.Name dialect, TransactionManager transactionManager, JsonMapper jsonMapper, String defaultSchema, DatabaseExternalDefintion ded, Module module, Module... modules) {
        this.dialect = Dialects.dialect(dialect, this);
        this.transactionManager = transactionManager;
        this.jsonMapper = jsonMapper;
        this.modules = new Module[1 + modules.length];
        this.modules[0] = module;
        if (modules.length > 0) {
            System.arraycopy(modules, 0, this.modules, 1, modules.length);
        }

        ImmutableMap.Builder<SqlTable, Module> builder = ImmutableMap.builder();
        ImmutableMap.Builder<SqlSequence, Module> builderSequences = ImmutableMap.builder();
        for (Module m : this.modules) {
            m.descriptors().forEach(d -> {
            	builder.put(d.table(), m);
            	for (SqlPrimaryKey pk : d.ids()) {
            		if (pk.sequence() != null) {
            			builderSequences.put(pk.sequence(), m);
            		}
            	}
            });
        }
        
        if (ded != null) {
            ded.forEachSequence( (m, sequence) -> {
                LOGGER.info("add External sequence [{}] to Module [{}]", sequence, module);
                builderSequences.put(sequence, m);
            });
        }
        
        tables = builder.build();
        sequences = builderSequences.build();

        postInit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionManager transactionManager() {
        return this.transactionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModule(SqlTable table) {
        return this.tables.get(table);
    }

    @Override
	public Module getModule(SqlSequence sequence) {
		return this.sequences.get(sequence);
	}
    
    @Override
	public JsonMapper jsonMapper() {
		return jsonMapper;
	}

    public void postInit() {
    	if (transactionManager instanceof TransactionManagerImpl) {
    		trace(((TransactionManagerImpl)transactionManager).getDataSource(), this.modules);
    	}
    			
    }

    private static void trace(DataSource dataSource, Module[] modules) {

        StringBuilder builder = new StringBuilder();
        builder.append("\n--------------------------------------------------------------------------------");
        try (Connection connection = dataSource.getConnection()) {

            DatabaseMetaData meta = connection.getMetaData();

            // @formatter:off
            builder.append("\nDatabase -> name : [").append(meta.getDatabaseProductName()).append("] -> version : [")
                    .append(meta.getDatabaseProductVersion()).append("] \n         -> Major [")
                    .append(meta.getDatabaseMajorVersion()).append("] -> Minor [")
                    .append(meta.getDatabaseMinorVersion()).append("]");
            builder.append("\nJDBC     -> name : [").append(meta.getDriverName()).append("] -> version : [")
                    .append(meta.getDriverVersion()).append("]").append("] \n         -> Major [")
                    .append(meta.getDriverMajorVersion()).append("] -> Minor [").append(meta.getDriverMinorVersion())
                    .append("]");

            // @formatter:on
            for (Module module : modules) {
                DatabaseSchemaChecker.checkModule(builder, meta, module);
            }

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        builder.append("\n--------------------------------------------------------------------------------");

        LOGGER.info("init database :{}", builder.toString());
    }

}
