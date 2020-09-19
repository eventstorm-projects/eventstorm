package eu.eventsotrm.core.apt.query;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.core.apt.model.QueryPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsQueryDatabaseProperty;
import eu.eventstorm.annotation.CqrsQueryPojoProperty;
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
		sourceCode.forEachDatabaseQueryPackage((pack, list) -> {
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
					
			} else if ("int".equals(epd.getter().getReturnType().toString()) || "long".equals(epd.getter().getReturnType().toString())) {
				writer.write("        gen.writeNumberField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
				writeNewLine(writer);	
			} else if (OffsetDateTime.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeOffsetDateTime(writer, epd);
			} else if (Json.class.getName().equals(epd.getter().getReturnType().toString())) {
				writeJson(writer, epd);
			} else {
				writer.write("        // write (" + epd.name() + "); " + epd.getter().getReturnType());
				writeNewLine(writer);
			}
			
		}
		
		writer.write("        gen.writeEndObject();");
		writeNewLine(writer);

		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
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
		throw new IllegalStateException();
	}
}