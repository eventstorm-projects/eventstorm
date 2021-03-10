package eu.eventstorm.core.apt.query;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsQueryDatabaseProperty;
import eu.eventstorm.annotation.CqrsQueryPojoProperty;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.type.Json;
import eu.eventstorm.util.Dates;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryJacksonStdSerializerGenerator {

	private final Logger logger;

	private final AtomicInteger counter = new AtomicInteger(1);
	
	public QueryJacksonStdSerializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryJacksonStdSerializerGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
		sourceCode.forEachDatabaseViewQueryPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

		sourceCode.forEachDatabaseTableQueryPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

		sourceCode.forEachPojoQueryPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
	}

	private void generate(ProcessingEnvironment env, String pack, ImmutableList<? extends QueryDescriptor> descriptors) throws IOException {

		for (QueryDescriptor ed : descriptors) {
		    
		    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(pack + ".json." + ed.simpleName() + "StdSerializer") != null) {
                logger.info("Java SourceCode already exist [" + pack + ".json." + ed.simpleName() + "StdSerializer" + "]");
                return;
            }
            
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + ed.simpleName() + "StdSerializer");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".json", ed);
		    writeConstructor(writer, ed);
			writeMethod(writer, ed);

			writer.write("}");
			writer.close();
		}

	}

	private static void writeHeader(Writer writer, String pack, QueryDescriptor descriptor) throws IOException {
		writePackage(writer, pack);
		
		writer.write("import " + StdSerializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + JsonGenerator.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SerializerProvider.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + IOException.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, QueryJacksonStdSerializerGenerator.class.getName());
		writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
		writer.write("final class "+ descriptor.simpleName() +"StdSerializer extends StdSerializer<"+ descriptor.simpleName() +"> {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer, QueryDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    " + ed.simpleName() +"StdSerializer");
		writer.write("() {");
		writeNewLine(writer);
		writer.write("        super("+ ed.simpleName()+".class);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethod(Writer writer, QueryDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public void serialize(" + ed.simpleName() + " payload, JsonGenerator gen, SerializerProvider provider) throws IOException {");
		writeNewLine(writer);
		
		writer.write("        gen.writeStartObject();");
		writeNewLine(writer);
		
		for (QueryPropertyDescriptor epd : ed.properties()) {
			
			if ("java.lang.String".equals(epd.getter().getReturnType().toString())) {
			
				if (isNullable(epd)) {
					writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
					writeNewLine(writer);
					writer.write("            gen.writeStringField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
					writeNewLine(writer);
					writer.write("        }");
					writeNewLine(writer);

				} else {
					writer.write("        gen.writeStringField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
					writeNewLine(writer);
				}
					
			} else if ("int".equals(epd.getter().getReturnType().toString()) || "long".equals(epd.getter().getReturnType().toString())
					|| "short".equals(epd.getter().getReturnType().toString()) || "byte".equals(epd.getter().getReturnType().toString())) {
				writer.write("        gen.writeNumberField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
				writeNewLine(writer);	
			} else if (Integer.class.equals(epd.getter().getReturnType().toString()) || Long.class.equals(epd.getter().getReturnType().toString())
					|| Short.class.equals(epd.getter().getReturnType().toString()) || Byte.class.equals(epd.getter().getReturnType().toString())) {
				writeNumber(writer, epd);
			} else if (OffsetDateTime.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeOffsetDateTime(writer, epd);
			} else if (Json.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeJson(writer, epd);
			} else if (Timestamp.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeTimestamp(writer, epd);
			} else if (Boolean.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeBoolean(writer, epd);
			} else if (boolean.class.getName().equals(epd.getter().getReturnType().toString())) {
				writer.write("        gen.writeBooleanField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
			}else {
				writer.write("        gen.writeObjectField(\"" + epd.name() + "\", payload." + epd.getter().getSimpleName()+"());");
				writeNewLine(writer);
			}
			
		}
		
		writer.write("        gen.writeEndObject();");
		writeNewLine(writer);

		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeTimestamp(Writer writer, QueryPropertyDescriptor epd) throws IOException {
		if (isNullable(epd)) {
			writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
			writeNewLine(writer);
			writer.write("            gen.writeStringField(\"" + epd.name() + "\", "+ Dates.class.getName()+".format(payload."+ epd.getter().getSimpleName() +"().toLocalDateTime()));");
			writeNewLine(writer);
			writer.write("        }");
			writeNewLine(writer);
		} else {
			writer.write("        gen.writeStringField(\"" + epd.name() + "\", "+ Dates.class.getName()+".format(payload."+ epd.getter().getSimpleName() +"().toLocalDateTime()));");
			writeNewLine(writer);
		}
	}

	private void writeBoolean(Writer writer, QueryPropertyDescriptor epd) throws IOException {
		if (isNullable(epd)) {
			writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
			writeNewLine(writer);
			writer.write("            gen.writeBooleanField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
			writeNewLine(writer);
			writer.write("        }");
			writeNewLine(writer);
		} else {
			writer.write("            gen.writeBooleanField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
			writeNewLine(writer);
		}
	}

	private void writeNumber(Writer writer, QueryPropertyDescriptor epd) throws IOException {
		if (isNullable(epd)) {
			writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
			writeNewLine(writer);
			writer.write("            gen.writeNumberField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
			writeNewLine(writer);
			writer.write("        }");
			writeNewLine(writer);
		} else {
			writer.write("            gen.writeNumberField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
			writeNewLine(writer);
		}
	}

	private void writeOffsetDateTime(Writer writer, QueryPropertyDescriptor epd) throws IOException {
		if (isNullable(epd)) {
			writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
			writeNewLine(writer);
			writer.write("            gen.writeStringField(\"" + epd.name() + "\", "+ Dates.class.getName()+".format(payload."+ epd.getter().getSimpleName() +"()));");
			writeNewLine(writer);
			writer.write("        }");
			writeNewLine(writer);
		} else {
			writer.write("        gen.writeStringField(\"" + epd.name() + "\", "+ Dates.class.getName()+".format(payload."+ epd.getter().getSimpleName() +"()));");
			writeNewLine(writer);	
		}
	}
	
	private void writeJson(Writer writer, QueryPropertyDescriptor epd) throws IOException {
		
		String var = "content_" + counter.getAndIncrement();
		String space = "";
		
		if (isNullable(epd)) {
			writer.write("        if (payload." +  epd.getter().getSimpleName()+ "() != null) {");
			space = "    ";
		}

		writer.write(space + "        gen.writeFieldName(\"" + epd.name() + "\");");
		writeNewLine(writer);
		writer.write(space + "        byte[] " + var + " = payload." + epd.getter().getSimpleName() +"().write(null);");
		writeNewLine(writer);
		writer.write(space + "        gen.writeRawValue(new String("+var+", 0, "+var+".length));");
		writeNewLine(writer);
		
		if (isNullable(epd)) {
			writer.write("        }");
			writeNewLine(writer);
		}
		
	}
	
	private static boolean isNullable(QueryPropertyDescriptor epd) {
		CqrsQueryDatabaseProperty property = epd.getter().getAnnotation(CqrsQueryDatabaseProperty.class);
		if (property != null) {
			return property.column().nullable();
		}
		CqrsQueryPojoProperty prop2 = epd.getter().getAnnotation(CqrsQueryPojoProperty.class);
		if (prop2 != null) {
			return prop2.nullable();
		}

		Column column = epd.getter().getAnnotation(Column.class);
		if (column != null) {
			return column.nullable();
		}

		if (epd.getter().getAnnotation(PrimaryKey.class)!= null) {
			return false;
		}

		throw new IllegalStateException();
	}
}