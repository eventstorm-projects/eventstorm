package eu.eventsotrm.sql.apt.flyway;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventsotrm.sql.apt.util.Tuple;
import eu.eventstorm.sql.annotation.AutoIncrement;
import eu.eventstorm.sql.annotation.BusinessKey;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.Db;
import eu.eventstorm.sql.annotation.Flyway;
import eu.eventstorm.sql.annotation.Index;
import eu.eventstorm.sql.annotation.JoinColumn;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.annotation.UpdateTimestamp;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class FlywayGenerator {

	private final Logger logger;

	private final Map<String, Tuple<FileObject,Writer>> holders = new HashMap<>();
	
	private final List<DeferWriter> indexes = new ArrayList<>();
	private final List<DeferWriter> sequences = new ArrayList<>();

	public FlywayGenerator() {
		logger = LoggerFactory.getInstance().getLogger(FlywayGenerator.class);
	}

	public void generate(ProcessingEnvironment env, List<GlobalConfigurationDescriptor> configs) {
		for (GlobalConfigurationDescriptor gcd : configs) {
			try {
				generate(env, gcd);
			} catch (Exception cause) {
				logger.error("", cause);
			}

		}
	}

	private void generate(ProcessingEnvironment env, GlobalConfigurationDescriptor gcd) {

		logger.info("Generate GlobalConfiguration for gcd");
		
		ImmutableMap.Builder<String,Flyway> builder = ImmutableMap.builder();
		for (Flyway flyway : gcd.getGlobalConfiguration().flywayConfiguration().flyways()) {
			builder.put(flyway.version(), flyway);
		}
		ImmutableMap<String,Flyway> map = builder.build();

		for (Db db : gcd.getGlobalConfiguration().flywayConfiguration().database()) {
			
			logger.info("Generate for DB [" + db + "]");
			
			gcd.getDescriptors().forEach(pojo -> {

				logger.info("Generate for pojo [" + pojo + "]");
				
				Flyway flyway;
				if (pojo.getTable() != null) {
					flyway = map.get(pojo.getTable().flywayRef().version());
				} else {
					flyway = map.get(pojo.getJoinTable().flywayRef().version());
				}
				
				logger.info("Generate for version [" + flyway + "]");

				if (flyway.version().trim().length() > 0) {
					FlywayDialect fd = FlywayDialects.get(db);
					try {
						generate(env, pojo, flyway, fd, db);
					} catch (IOException cause) {
						logger.error("Failed to generate [" + flyway + "] -> " + db, cause);
					}
				}

			});
			
			this.sequences.forEach(DeferWriter::write);
			this.indexes.forEach(DeferWriter::write);
			
			
			holders.forEach((filename, tuple) -> {
				try {
					LoggerFactory.getInstance().getLogger(FlywayGenerator.class).info("to close [" + tuple.getX().toUri() + "]");
					tuple.getY().close();
				} catch (IOException cause) {
					LoggerFactory.getInstance().getLogger(FlywayGenerator.class).error("Failed to close [" + tuple + "]", cause);
				}
			});
			
			holders.clear();
			indexes.clear();
			sequences.clear();
		}
			
		
	}


	private void generate(ProcessingEnvironment env, PojoDescriptor descriptor, Flyway flyway, FlywayDialect fd, Db db) throws IOException {

		List<String> businessKeys = new ArrayList<>();

		String filename = "V" + flyway.version() + "__" + flyway.description() + ".sql";

		Tuple<FileObject, Writer> tuple = holders.get(filename);

		if (tuple == null) {
			FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "db.migration." + db.name().toLowerCase(), filename);
			Writer writer = object.openWriter();
			tuple = new Tuple<>(object, writer);
			this.holders.put(filename, tuple);
		}


		Writer writer = tuple.getY();


		if (descriptor.getTable() != null) {
			generateTable(descriptor, fd, businessKeys, writer, descriptor.getTable());
			return;
		}

		if (descriptor.getJoinTable() != null) {
			generateJoinTable(descriptor, fd, businessKeys, writer, descriptor.getJoinTable());
			return;
		}

	}

	private void generateTable(PojoDescriptor descriptor, FlywayDialect fd, List<String> businessKeys, Writer writer,
			Table table) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ");
		builder.append(fd.wrap(table.value()));
		builder.append(" (");

		StringBuilder primaryKey = new StringBuilder();
		primaryKey.append("PRIMARY KEY (");

		if (descriptor.ids().size() > 0) {
			builder.append("\n   ");
			builder.append("-- PRIMARY KEY");
			for (PojoPropertyDescriptor id : descriptor.ids()) {
				
				builder.append("\n   ");
				PrimaryKey anno = id.getter().getAnnotation(PrimaryKey.class);
				String columnName = anno.value();
				builder.append(fd.wrap(columnName));
				for (int i = columnName.length() ; i < 24 ; i++) {
					builder.append(' ');
				}

				AutoIncrement autoIncrement = id.getter().getAnnotation(AutoIncrement.class);
				if (autoIncrement != null) {
					builder.append(fd.autoIncrementType(id.getter().getReturnType().toString()));
				} else {
					builder.append(fd.toSqlType(id.getter().getReturnType().toString(), anno));
				}
				builder.append(",");

				primaryKey.append(fd.wrap(anno.value())).append(',');

				if (id.getter().getAnnotation(Sequence.class) != null) {
					generateSequence(writer, id.getter().getAnnotation(Sequence.class), fd);
				}

			}
			primaryKey.deleteCharAt(primaryKey.length()-1).append(')');	
		}

		if (descriptor.properties().size() > 0) {
			builder.append("\n   ");
			builder.append("-- COLUMNS");
			for (PojoPropertyDescriptor col : descriptor.properties()) {
				builder.append("\n   ");
				String columnName = Helper.getSqlColumnName(col);
				builder.append(fd.wrap(columnName));
				for (int i = columnName.length() ; i < 24 ; i++) {
					builder.append(' ');
				}
				
				Column anno = col.getter().getAnnotation(Column.class);
				String type = fd.toSqlType(col.getter().getReturnType().toString(), anno);
				builder.append(type);
				for (int i = type.length() ; i < 16 ; i++) {
					builder.append(' ');
				}
				
				if (!anno.nullable() && col.getter().getAnnotation(UpdateTimestamp.class) == null) {
					builder.append(" NOT NULL");
				}

				builder.append(",");

				BusinessKey bk = col.getter().getAnnotation(BusinessKey.class);
				if (bk != null) {
					businessKeys.add(columnName);
				}
			}
			builder.deleteCharAt(builder.length()-1);
		}
		
		if (descriptor.ids().size() > 0) {
			builder.append(",\n   ");
			builder.append(primaryKey);	
			builder.deleteCharAt(builder.length()-1);
			builder.append(')');
		}
		
		builder.append("\n);\n");

		writer.append(builder.toString());

		if (businessKeys.size() > 0) {
			generateUniqueIndex(table, businessKeys, writer);
		}
		
		generateIndex(table, writer);
	}

	

	private void generateJoinTable(PojoDescriptor descriptor, FlywayDialect fd, List<String> businessKeys, Writer writer,
			JoinTable table) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ");
		builder.append(fd.wrap(table.value()));
		builder.append(" (");

		StringBuilder primaryKey = new StringBuilder();
		primaryKey.append("PRIMARY KEY (");

		for (PojoPropertyDescriptor id : descriptor.ids()) {
			builder.append("\n   ");
			JoinColumn anno = id.getter().getAnnotation(JoinColumn.class);
			builder.append(fd.wrap(anno.value()));
			builder.append("   ");

			// TODO search joinColumn to id target;

			//builder.append(fd.toSqlType(id.getter().getReturnType().toString(), id.getter().getAnnotation(JoinColumn.class)));
			builder.append(" INT" );
			builder.append(",");

			primaryKey.append(fd.wrap(anno.value())).append(",");

		}
		primaryKey.deleteCharAt(primaryKey.length()-1).append("),");

		for (PojoPropertyDescriptor col : descriptor.properties()) {
			builder.append("\n   ");
			
			String columnName = Helper.getSqlColumnName(col);
			builder.append(fd.wrap(columnName));
			for (int i = columnName.length() ; i < 24 ; i++) {
				builder.append(' ');
			}
			String type = fd.toSqlType(col.getter().getReturnType().toString(), col.getter().getAnnotation(Column.class));
			builder.append(type);
			for (int i = type.length() ; i < 16 ; i++) {
				builder.append(' ');
			}
			if (!col.getter().getAnnotation(Column.class).nullable()) {
				builder.append(" NOT NULL");
			}

			builder.append(",");

			BusinessKey bk = col.getter().getAnnotation(BusinessKey.class);
			if (bk != null) {
				businessKeys.add(columnName);
			}
		}

		builder.append("\n   ");
		builder.append(primaryKey);

		builder.deleteCharAt(builder.length() - 1);
		builder.append("\n);\n");

		writer.append(builder.toString());
	}

	private void generateSequence(Writer writer, Sequence sequence, FlywayDialect fd) {
		logger.info("generate sequence " + sequence);
		this.sequences.add(new DeferWriter(writer,  "CREATE SEQUENCE " + fd.wrap(sequence.value())  + ";\n"));
	}

	private void generateIndex(Table table, Writer writer) throws IOException {
		
		for (Index index : table.indexes()) {
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE INDEX ");
			builder.append(index.name());
			builder.append(" ON ");
			builder.append(table.value());
			builder.append("(");
			for (String column : index.columns()) {
				builder.append(column).append(',');				
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append(");\n");

			indexes.add(new DeferWriter(writer, builder.toString()));
		}
	}
	private void generateUniqueIndex(Table table, List<String> businessKeys, Writer writer) throws IOException {

		StringBuilder builder = new StringBuilder();
		builder.append("CREATE UNIQUE INDEX ");
		builder.append(table.value());
		builder.append("_bk");
		builder.append(" ON ");
		builder.append(table.value());
		builder.append("(");
		businessKeys.forEach(bk -> {
			builder.append(bk).append(',');
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(");\n");

		indexes.add(new DeferWriter(writer, builder.toString()));
	}

	
	private class DeferWriter {

		private final Writer writer;
		private final String ddl;
		DeferWriter(Writer writer, String ddl) {
			this.writer = writer;
			this.ddl = ddl;
		}
		void write() {
			try {
				this.writer.write(ddl);
			} catch (IOException cause) {
				logger.error("failed to writer defer ddl", cause);
			}
			
		}
		
	}

}
