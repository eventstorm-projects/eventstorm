package eu.eventsotrm.core.apt.query;

import static eu.eventsotrm.sql.apt.Helper.toUpperCase;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.function.Function;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.DatabaseQueryDescriptor;
import eu.eventsotrm.core.apt.model.QueryPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.cqrs.SqlQueryDescriptor;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.page.PreparedStatementIndexSetter;
import eu.eventstorm.sql.util.Dates;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryDescriptorGenerator {

	private final Logger logger;

	public QueryDescriptorGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDescriptorGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
		sourceCode.forEachDatabaseQueryPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

	private void generate(ProcessingEnvironment env, String pack, ImmutableList<DatabaseQueryDescriptor> descriptors) throws IOException {

		for (DatabaseQueryDescriptor ed : descriptors) {
		    
			String fcqn = pack + "." + ed.simpleName() + "SqlQueryDescriptor";
			
		    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(fcqn) != null) {
                logger.info("Java SourceCode already exist [" + fcqn + "]");
                return;
            }
            
			JavaFileObject object = env.getFiler().createSourceFile(fcqn);
			Writer writer = object.openWriter();

			writeHeader(writer, pack , ed);
			writeStatic(writer, ed);
		    writeConstructor(writer, ed);
			writeMethodGet(writer, ed);
			writeMethodExpression(writer, ed);

			writer.write("}");
			writer.close();
		}

	}

	private static void writeHeader(Writer writer, String pack, DatabaseQueryDescriptor descriptor) throws IOException {
		writePackage(writer, pack);
		

		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + Function.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SqlColumn.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SqlQueryDescriptor.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + PreparedStatementIndexSetter.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, QueryDescriptorGenerator.class.getName());
		writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
		writer.write("public final class "+ descriptor.simpleName() +"SqlQueryDescriptor implements SqlQueryDescriptor {");
		writeNewLine(writer);
	}
	

	private void writeStatic(Writer writer, DatabaseQueryDescriptor ed) throws IOException {
		
		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, SqlColumn> VALUES = ImmutableMap.<String, SqlColumn>builder() ");
		writeNewLine(writer);
		for (QueryPropertyDescriptor property :  ed.properties()) {
			writer.write("        .put(\"" + property.name() + "\", " + ed.fullyQualidiedClassName() + "Descriptor." + toUpperCase(property.name()) + ")");
			writeNewLine(writer);
		}
		writer.write("        .build();");
		writeNewLine(writer);
		
		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, Function<String, PreparedStatementIndexSetter>> PREPARED_STATEMENT_INDEX_SETTERS");
		writeNewLine(writer);
		writer.write("        = ImmutableMap.<String, Function<String, PreparedStatementIndexSetter>>builder()");
		writeNewLine(writer);
		
		// private static final Function<String,String> COUNTRY = t -> t;
		for (QueryPropertyDescriptor property :  ed.properties()) {
			String type = Helper.getReturnType(property.getter());
			if (Helper.isInteger(type)) {
				writer.write("            .put(\"" + property.name() + "\", val -> (ps,index) -> ps.setInt(index, Integer.valueOf(val)))");
			} else if (Helper.isLong(type)) {
				writer.write("            .put(\"" + property.name() + "\", val -> (ps,index) -> ps.setLong(index, Long.valueOf(val)))");
			} else if (Helper.isBoolean(type)) {
				writer.write("            .put(\"" + property.name() + "\", val -> (ps,index) -> ps.setBoolean(index, Boolean.valueOf(val)))");
			} else if (Helper.isString(type)) {
				writer.write("            .put(\"" + property.name() + "\", val -> (ps,index) -> ps.setString(index, val))");
			} else if (Timestamp.class.getName().equals(type)) {
				writer.write("            .put(\"" + property.name() + "\", val -> (ps,index) -> ps.setTimestamp(index, "+ Dates.class.getName()+".convertTimestamp(val)))");
			}
			writeNewLine(writer);
		}
		writer.write("        .build();");
		writeNewLine(writer);
		writeNewLine(writer);
	}

	
	private static void writeConstructor(Writer writer, DatabaseQueryDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    public " + ed.simpleName() +"SqlQueryDescriptor");
		writer.write("() {");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethodGet(Writer writer, DatabaseQueryDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public SqlColumn get(String property) {");
		writeNewLine(writer);
		writer.write("        return VALUES.get(property);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}
	
	private void writeMethodExpression(Writer writer, DatabaseQueryDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public PreparedStatementIndexSetter getPreparedStatementIndexSetter(String property, String value) {");
		writeNewLine(writer);
		writer.write("        return PREPARED_STATEMENT_INDEX_SETTERS.get(property).apply(value);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}