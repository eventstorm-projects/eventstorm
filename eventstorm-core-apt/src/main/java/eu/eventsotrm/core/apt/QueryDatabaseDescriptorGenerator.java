package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.toUpperCase;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.core.apt.model.QueryPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.core.annotation.CqrsQueryDatabase;
import eu.eventstorm.sql.Descriptor;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class QueryDatabaseDescriptorGenerator {

	/**
	 * key for properties (holder of all aliases generated).
	 */
	public static final String KEY = "pojo.descriptor";

	private final Logger logger;

	QueryDatabaseDescriptorGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDatabaseDescriptorGenerator.class);
	}

    public void generate(ProcessingEnvironment env, QueryDescriptor queryDescriptor) {

        try {
            JavaFileObject object = env.getFiler().createSourceFile(queryDescriptor.fullyQualidiedClassName() + "DatabaseDescriptor");
            Writer writer = object.openWriter();

            writeHeader(writer, env, queryDescriptor);
            writeSingleton(writer, queryDescriptor);
            writeConstructor(writer, queryDescriptor);
            writeTable(writer, queryDescriptor);
            writeProperties(writer, queryDescriptor);
            writeAllColumns(writer, queryDescriptor);
            writeAllSingleColumns(writer, queryDescriptor);
            writeMethods(writer, queryDescriptor);

            writer.write("}");
            writer.close();
        } catch (Exception cause) {
            logger.error("Exception for [" + queryDescriptor + "] -> [" + cause.getMessage() + "]", cause);
        }

    }


	private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryDescriptor descriptor)
			throws IOException {
		writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
		writeGenerated(writer, QueryDatabaseDescriptorGenerator.class.getName());

		writer.write("public final class ");
		writer.write(descriptor.simpleName() + "DatabaseDescriptor implements ");
		writer.write(Descriptor.class.getName());
		writer.write(" {");
		writeNewLine(writer);
	}

	private static void writeSingleton(Writer writer, QueryDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    static final ");
		writer.write(Descriptor.class.getName());
		writer.write(" INSTANCE = new ");
		writer.write(descriptor.simpleName() + "Descriptor();");
		writeNewLine(writer);

	}

	private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
		writeNewLine(writer);
		writer.write("    private ");
		writer.write(descriptor.simpleName() + "DatabaseDescriptor");
		writer.write(" () {");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private static void writeTable(Writer writer, QueryDescriptor descriptor) throws IOException {
		writeNewLine(writer);
		writer.write("    // SQL TABLE DESCRIPTOR");
		writeNewLine(writer);
		writer.write("    public static final ");
		writer.write(SqlTable.class.getName());
		writer.write(" TABLE = new ");
		writer.write(SqlTable.class.getName());
		writer.write("(\"");

		writer.write(descriptor.element().getAnnotation(CqrsQueryDatabase.class).table().value());

		writer.write("\", \"");
		//writer.write(generateAlias(properties));
		writer.write("\");");
		writeNewLine(writer);
	}

	


	private static void writeProperties(Writer writer, QueryDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // SQL PROPERTIES");
		writeNewLine(writer);

		for (QueryPropertyDescriptor ppd : descriptor.properties()) {

			Column column = ppd.getter().getAnnotation(Column.class);

			writer.write("    public static final ");
			writer.write(SqlSingleColumn.class.getName());
			writer.write(" ");
			writer.write(toUpperCase(ppd.name()));
			writer.write(" = new ");
			writer.write(SqlSingleColumn.class.getName());
			writer.write("(TABLE, \"");
			//writer.write(Helper.getSqlColumnName(ppd));
			writer.write("\", ");
			writer.write("" + column.nullable());
			writer.write(", ");
			writer.write("" + column.insertable());
			writer.write(", ");
			writer.write("" + column.updatable());
			writer.write(");");
			writeNewLine(writer);
		}

	}

	private static void writeAllColumns(Writer writer, QueryDescriptor queryDescriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // ALL COLUMNS");
		writeNewLine(writer);

		writer.write("    public static final ");
		writer.write(ImmutableList.class.getName());
		writer.write("<");
		writer.write(SqlColumn.class.getName());
		writer.write("> ALL = ");
		writer.write(ImmutableList.class.getName());
		writer.write(".of(");

		StringBuilder builder = new StringBuilder();



		for (QueryPropertyDescriptor id : queryDescriptor.properties()) {
			writeNewLine(builder);
			builder.append("            ");
			builder.append(toUpperCase(id.name()));
			builder.append(',');

		}

		builder.deleteCharAt(builder.length() - 1);

		writer.write(builder.toString());
		writer.write(");");
		writeNewLine(writer);

	}

	

	private static void writeMethods(Writer writer, QueryDescriptor queryDescriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    public ");
		writer.write(SqlTable.class.getName());
		writer.write(" table() {");
		writeNewLine(writer);
		writer.write("        return TABLE;");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

		writeNewLine(writer);
		writer.write("    public ");
		writer.write(ImmutableList.class.getName());
		writer.write("<");
		writer.write(SqlSingleColumn.class.getName());
		writer.write("> columns() {");
		writeNewLine(writer);
		writer.write("        return COLUMNS;");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

		writeNewLine(writer);
		writer.write("    public ");
		writer.write(ImmutableList.class.getName());
		writer.write("<");
		writer.write(SqlPrimaryKey.class.getName());
		writer.write("> ids() {");
		writeNewLine(writer);
		writer.write("        return IDS;");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

	}

	private static void writeAllSingleColumns(Writer writer, QueryDescriptor queryDescriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // ALL SINGLE COLUMNS");
		writeNewLine(writer);

		writer.write("    public static final ");
		writer.write(ImmutableList.class.getName());
		writer.write("<");
		writer.write(SqlSingleColumn.class.getName());
		writer.write("> COLUMNS = ");
		writer.write(ImmutableList.class.getName());
		writer.write(".of(");

		StringBuilder builder = new StringBuilder();

		for (QueryPropertyDescriptor id : queryDescriptor.properties()) {
			writeNewLine(builder);
			builder.append("            ");
			builder.append(toUpperCase(id.name()));
			builder.append(',');

		}

		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
			writer.write(builder.toString());
		}

		writer.write(");");
		writeNewLine(writer);

	}

	private static String generateAlias(Map<String, Object> properties) {

		String current = (String) properties.get(KEY);

		if (current == null) {
			properties.put(KEY, "a");
			return "a";
		}

		if ("z".equals(current)) {
			properties.put(KEY, "aa");
			return "aa";
		}

		if ("zz".equals(current)) {
			properties.put(KEY, "aaa");
			return "aaa";
		}

		if ("zzz".equals(current)) {
			properties.put(KEY, "aaaa");
			return "aaaa";
		}

		StringBuilder builder = new StringBuilder();

		if (current.length() > 1) {
			builder.append(current.substring(0, current.length() - 2));
		}
		builder.append((char) (current.charAt(current.length() - 1) + 1));

		return builder.toString();
	}
}
