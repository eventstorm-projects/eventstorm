package eu.eventsotrm.sql.apt;

import static eu.eventsotrm.sql.apt.Helper.toUpperCase;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.Descriptor;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.JoinColumn;
import eu.eventstorm.sql.annotation.JoinTable;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoDescriptorGenerator implements Generator {

	/**
	 * key for properties (holder of all aliases generated).
	 */
	public static final String KEY = "pojo.descriptor";

	private final Logger logger;

    private static final Map<String, Object> properties = new HashMap<>();

	PojoDescriptorGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoImplementationGenerator.class);
	}

	 public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

		// generate Implementation class;
		sourceCode.forEach(t -> {
			try {
				generate(env, t);
			} catch (Exception cause) {
				logger.error("PojoImplementationGenerator -> IOException for [" + t + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

        properties.clear();

	}

	private void generate(ProcessingEnvironment env, PojoDescriptor descriptor)
			throws IOException {

		JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Descriptor");
		Writer writer = object.openWriter();

		writeHeader(writer, env, descriptor);
		writeSingleton(writer, descriptor);
		writeConstructor(writer, descriptor);
		writeTable(writer, descriptor);
		writeSequence(writer, descriptor);
		writePrimaryKeys(writer, descriptor);
		writeProperties(writer, descriptor);
		writeAllColumns(writer, descriptor);
		writeAllIndexes(writer, descriptor);
		writeAllIds(writer, descriptor);
		writeAllSingleColumns(writer, descriptor);
		writeMethods(writer, descriptor);

		writer.write("}");
		writer.close();
	}

	private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor)
			throws IOException {
		writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
		writeGenerated(writer, PojoDescriptorGenerator.class.getName());

		writer.write("public final class ");
		writer.write(descriptor.simpleName() + "Descriptor implements ");
		writer.write(Descriptor.class.getName());
		writer.write(" {");
		writeNewLine(writer);
	}

	private static void writeSingleton(Writer writer, PojoDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    static final ");
		writer.write(Descriptor.class.getName());
		writer.write(" INSTANCE = new ");
		writer.write(descriptor.simpleName() + "Descriptor();");
		writeNewLine(writer);

	}

	private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
		writeNewLine(writer);
		writer.write("    private ");
		writer.write(descriptor.simpleName() + "Descriptor");
		writer.write(" () {");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private static void writeTable(Writer writer, PojoDescriptor descriptor) throws IOException {
		writeNewLine(writer);
		writer.write("    // SQL TABLE DESCRIPTOR");
		writeNewLine(writer);
		writer.write("    public static final ");
		writer.write(SqlTable.class.getName());
		writer.write(" TABLE = new ");
		writer.write(SqlTable.class.getName());
		writer.write("(\"");

		if (descriptor.element().getAnnotation(Table.class) != null) {
			writer.write(descriptor.element().getAnnotation(Table.class).value());
		} else if (descriptor.element().getAnnotation(JoinTable.class) != null) {
			writer.write(descriptor.element().getAnnotation(JoinTable.class).value());
		}

		writer.write("\", \"");
		writer.write(generateAlias(properties));
		writer.write("\");");
		writeNewLine(writer);
	}

	private static void writeSequence(Writer writer, PojoDescriptor descriptor) throws IOException {

		if (descriptor.element().getAnnotation(Table.class) == null) {
			// not a table, maybe jointTable, ...
			return;
		}
		boolean hasSequence = false;
		for (PojoPropertyDescriptor id : descriptor.ids()) {

			if (id.getter().getAnnotation(Sequence.class) != null) {
				if (hasSequence) {
					throw new SqlProcessorException("Failed to generate PojoDescriptor for [" + descriptor
							+ "] -> pojo has more than 1 sequence !");
				}
				writeNewLine(writer);
				writer.write("    // SQL SEQUENCE DESCRIPTOR");
				writeNewLine(writer);
				writer.write("    public static final ");
				writer.write(SqlSequence.class.getName());
				writer.write(" SEQUENCE = new ");
				writer.write(SqlSequence.class.getName());
				writer.write("(\"");
				writer.write(id.getter().getAnnotation(Sequence.class).value());
				writer.write("\");");
				writeNewLine(writer);
				hasSequence = true;
			}
		}
	}

	private static void writePrimaryKeys(Writer writer, PojoDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // SQL PRIMARY KEY");
		writeNewLine(writer);

		for (PojoPropertyDescriptor id : descriptor.ids()) {
			writer.write("    public static final ");
			writer.write(SqlPrimaryKey.class.getName());
			writer.write(" ");
			writer.write(toUpperCase(id.name()));
			writer.write(" = new ");
			writer.write(SqlPrimaryKey.class.getName());
			writer.write("(TABLE, ");

			if (id.getter().getAnnotation(PrimaryKey.class) != null) {
				if (id.getter().getAnnotation(Sequence.class) != null) {
					writer.write(" SEQUENCE, \"");
				} else {
					writer.write(" null, \"");
				}
				writer.write(id.getter().getAnnotation(PrimaryKey.class).value());
			} else if (id.getter().getAnnotation(JoinColumn.class) != null) {
				writer.write(" null, \"");
				writer.write(id.getter().getAnnotation(JoinColumn.class).value());
			}

			writer.write("\");");
			writeNewLine(writer);
		}
	}

	private static void writeProperties(Writer writer, PojoDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // SQL PROPERTIES");
		writeNewLine(writer);

		for (PojoPropertyDescriptor ppd : descriptor.properties()) {

			Column column = ppd.getter().getAnnotation(Column.class);

			writer.write("    public static final ");
			writer.write(SqlSingleColumn.class.getName());
			writer.write(" ");
			writer.write(toUpperCase(ppd.name()));
			writer.write(" = new ");
			writer.write(SqlSingleColumn.class.getName());
			writer.write("(TABLE, \"");
			writer.write(Helper.getSqlColumnName(ppd));
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

	private static void writeAllIndexes(Writer writer, PojoDescriptor descriptor) throws IOException {

	    writeNewLine(writer);
        writer.write("    // ALL COLUMNS INDEXES");
        writeNewLine(writer);

        int i = 1;

        for (PojoPropertyDescriptor id : descriptor.ids()) {
            writer.write("    public static final int INDEX__");
            writer.write(toUpperCase(id.name()));
            writer.write(" = " + i++ + ";");
            writeNewLine(writer);
        }

        for (PojoPropertyDescriptor id : descriptor.properties()) {
            writer.write("    public static final int INDEX__");
            writer.write(toUpperCase(id.name()));
            writer.write(" = " + i++ + ";");
            writeNewLine(writer);
        }
        
	}

	
	private static void writeAllColumns(Writer writer, PojoDescriptor descriptor) throws IOException {

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

		for (PojoPropertyDescriptor id : descriptor.ids()) {
//            writer.write("        ");
//            writer.write(toUpperCase(id.name()));
//            writer.write(',');
//            writeNewLine(writer);
			writeNewLine(builder);
			builder.append("            ");
			builder.append(toUpperCase(id.name()));
			builder.append(',');

		}

		for (PojoPropertyDescriptor id : descriptor.properties()) {
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

	private static void writeAllIds(Writer writer, PojoDescriptor descriptor) throws IOException {

		writeNewLine(writer);
		writer.write("    // ALL IDS");
		writeNewLine(writer);

		writer.write("    public static final ");
		writer.write(ImmutableList.class.getName());
		writer.write("<");
		writer.write(SqlPrimaryKey.class.getName());
		writer.write("> IDS = ");
		writer.write(ImmutableList.class.getName());
		writer.write(".of(");

		StringBuilder builder = new StringBuilder();

		for (PojoPropertyDescriptor id : descriptor.ids()) {
//            writer.write("        ");
//            writer.write(toUpperCase(id.name()));
//            writer.write(',');
//            writeNewLine(writer);
			writeNewLine(builder);
			builder.append("            ");
			builder.append(toUpperCase(id.name()));
			builder.append(',');

		}

		if (descriptor.ids().size() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		

		writer.write(builder.toString());
		writer.write(");");
		writeNewLine(writer);

	}

	private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {

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

	private static void writeAllSingleColumns(Writer writer, PojoDescriptor descriptor) throws IOException {

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

		for (PojoPropertyDescriptor id : descriptor.properties()) {
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
