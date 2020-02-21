package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.function.BiConsumer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.EventPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.json.DeserializerException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadJacksonStdDeserializerGenerator {

	private final Logger logger;

	EventPayloadJacksonStdDeserializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadJacksonStdDeserializerGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
		sourceCode.forEachEventPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

	private void generate(ProcessingEnvironment env, String pack, ImmutableList<EventDescriptor> descriptors) throws IOException {

		for (EventDescriptor ed : descriptors) {
		    
		    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
	        if (env.getElementUtils().getTypeElement(pack + ".json." + ed.simpleName() + "StdDeserializer") != null) {
	            logger.info("Java SourceCode already exist [" + pack + ".json." + ed.simpleName() + "StdDeserializer" + "]");
	            return;
	        }
		    
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + ed.simpleName() + "StdDeserializer");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".json", ed);
			writeStatic(writer, ed);
		    writeConstructor(writer, ed);
			writeMethod(writer, ed);

			writer.write("}");
			writer.close();
		}

	}

	private static void writeHeader(Writer writer, String pack, EventDescriptor descriptor) throws IOException {
		writePackage(writer, pack);

		writeNewLine(writer);
		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + BiConsumer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + DeserializerException.class.getName() + ";");
		writeNewLine(writer);
		
		writer.write("import " + StdDeserializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + JsonParser.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + JsonToken.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + IOException.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + DeserializationContext.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + "Builder;");
		writeNewLine(writer);
		
		writeImport(writer, descriptor, OffsetDateTime.class.getName());
        writeImport(writer, descriptor, LocalDate.class.getName());
        writeImport(writer, descriptor, LocalTime.class.getName());
		
		writeGenerated(writer, EventPayloadJacksonStdDeserializerGenerator.class.getName());
		writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
		writer.write("final class "+ descriptor.simpleName() +"StdDeserializer extends StdDeserializer<"+ descriptor.simpleName() +"> {");
		writeNewLine(writer);
	}
	
	
	private static void writeImport(Writer writer, EventDescriptor ed, String fcqn) throws IOException {
        for (EventPropertyDescriptor epd : ed.properties()) {
            if (fcqn.equals(epd.getter().getReturnType().toString())) {
                writer.write("import " + fcqn + ";");
                writeNewLine(writer);
                break;
            }
        }
    }

	private void writeStatic(Writer writer, EventDescriptor ed) throws IOException {

		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, BiConsumer<JsonParser," + ed.simpleName() + "Builder>> FIELDS;");
		writeNewLine(writer);
		writer.write("    static {");
		writeNewLine(writer);
		writer.write("        FIELDS = ImmutableMap.<String, BiConsumer<JsonParser,"+ ed.simpleName() + "Builder>>builder()");
		for (EventPropertyDescriptor epd : ed.properties()) {
		    writer.write(".put(\"" + epd.name() + "\", (parser, builder) -> {");
			writeNewLine(writer);
		    writer.write("			try {");
		    writeNewLine(writer);
		    writer.write("				builder.with" + Helper.firstToUpperCase(epd.name()) + "(");
		    
		    String returnType = getReturnType(epd.getter());
		    
		    if ("java.lang.String".equals(returnType)) {
				writer.write("parser.nextTextValue()");
			} else if ("int".equals(returnType) || "java.lang.Integer".equals(returnType)) {
				writer.write("parser.nextIntValue(0)");
			} else if ("long".equals(returnType) || "java.lang.Long".equals(returnType)) {
				writer.write("parser.nextLongValue(0l)");
			} else if (OffsetDateTime.class.getName().equals(returnType)) {
				writer.write("eu.eventstorm.util.Dates.parseOffsetDateTime(parser.nextTextValue())");
			} else if (LocalDate.class.getName().equals(returnType)) {
                writer.write("eu.eventstorm.util.Dates.parseLocalDate(parser.nextTextValue())");
            } else if (LocalTime.class.getName().equals(returnType)) {
                writer.write("eu.eventstorm.util.Dates.parseLocalTime(parser.nextTextValue())");
            } else {
			    throw new UnsupportedOperationException("Type not supported [" + returnType + "]");
			}
			writer.write(");");
			writeNewLine(writer);
			writer.write("			} catch (IOException cause) {");
		    writeNewLine(writer);
		    writer.write("			    throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"field\",\""+ epd.name() +"\"), cause);");
		    writeNewLine(writer);
		    writer.write("			}");
		    
		   
		    writeNewLine(writer);
			writer.write("		})");
		}
		
		writer.write(".build();");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

	}

	
	private static void writeConstructor(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    " + ed.simpleName() +"StdDeserializer");
		writer.write("() {");
		writeNewLine(writer);
		writer.write("        super("+ ed.simpleName()+".class);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethod(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public " + ed.simpleName() + " deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {");


		writeNewLine(writer);
		writer.write("        " + ed.simpleName() + "Builder builder = new " + ed.simpleName() + "Builder();");
		
		writeNewLine(writer);
		writer.write("        if (JsonToken.START_OBJECT != p.currentToken()) {");
		writeNewLine(writer);
		writer.write("            throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"expected\",JsonToken.START_OBJECT,\"current\", p.currentToken()));");
		writeNewLine(writer);
		writer.write("        }");
		writeNewLine(writer);
		
		writer.write("        p.nextToken();");
		writeNewLine(writer);
		
		writer.write("        while (p.currentToken() != JsonToken.END_OBJECT) {");
		writeNewLine(writer);
		
		writer.write("            if (JsonToken.FIELD_NAME != p.currentToken()) {");
		writeNewLine(writer);
		writer.write("                throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"expected\",JsonToken.FIELD_NAME,\"current\", p.currentToken()));");
		writeNewLine(writer);
		writer.write("            }");
		writeNewLine(writer);
		writer.write("            BiConsumer<JsonParser," + ed.simpleName() + "Builder> consumer = FIELDS.get(p.currentName());");
		writeNewLine(writer);
		writer.write("            if (consumer == null) {");
		writeNewLine(writer);
		writer.write("                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of(\"field\",p.currentName(),\"eventPayload\", \""+ ed.simpleName()+"\"));");
		writeNewLine(writer);
		writer.write("            }");
		writeNewLine(writer);
		writer.write("            consumer.accept(p, builder);");
		writeNewLine(writer);
		writer.write("            p.nextToken();");
		writeNewLine(writer);
		writer.write("        }");

		writeNewLine(writer);
		writer.write("        return builder.build();");
		writeNewLine(writer);
		writer.write("     }");
		writeNewLine(writer);
	}

}