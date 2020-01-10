package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.json.DeserializerException;
import eu.eventstorm.core.json.ParserConsumer;
import eu.eventstorm.util.Dates;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandJacksonStdDeserializerGenerator {

	private final Logger logger;

	CommandJacksonStdDeserializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandJacksonStdDeserializerGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
		sourceCode.forEachCommandPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

	private void generate(ProcessingEnvironment env, String pack, ImmutableList<CommandDescriptor> descriptors) throws IOException {

		for (CommandDescriptor cd : descriptors) {
		    
		    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
	        if (env.getElementUtils().getTypeElement(pack + ".json." + cd.simpleName() + "StdDeserializer") != null) {
	            logger.info("Java SourceCode already exist [" +pack + ".json." + cd.simpleName() + "StdDeserializer" + "]");
	            return;
	        }
	        
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + cd.simpleName() + "StdDeserializer");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".json", cd);
			writeStatic(writer, cd);
		    writeConstructor(writer, cd);
			writeMethod(writer, cd);

			writer.write("}");
			writer.close();
		}

	}

	private static void writeHeader(Writer writer, String pack, CommandDescriptor cd) throws IOException {
		writePackage(writer, pack);

		writeNewLine(writer);
		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ParserConsumer.class.getName() + ";");
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
		writer.write("import " + cd.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		writer.write("import " + cd.fullyQualidiedClassName() + "Builder;");
		writeNewLine(writer);
		writer.write("import " + cd.fullyQualidiedClassName().substring(0, cd.fullyQualidiedClassName().lastIndexOf('.') + 1) + "CommandFactory" + ";");
		writeNewLine(writer);
		
		writeImport(writer, cd, OffsetDateTime.class.getName(), Dates.class.getName() + ".parseOffsetDateTime");
		writeImport(writer, cd, LocalDate.class.getName(), Dates.class.getName() + ".parseLocalDate");
		writeImport(writer, cd, LocalTime.class.getName(), Dates.class.getName() + ".parseLocalTime");

		writeGenerated(writer, CommandJacksonStdDeserializerGenerator.class.getName());
		
	    writer.write("@SuppressWarnings(\"serial\")");
	    writeNewLine(writer);
		writer.write("final class "+ cd.simpleName() +"StdDeserializer extends StdDeserializer<"+ cd.simpleName() +"> {");
		writeNewLine(writer);
	}

	private static void writeImport(Writer writer, CommandDescriptor cd, String fcqn, String staticMethod) throws IOException {
	    for (CommandPropertyDescriptor cpd : cd.properties()) {
            if (fcqn.equals(cpd.getter().getReturnType().toString())) {
                writer.write("import static " + staticMethod + ";");
                writeNewLine(writer);
                break;
            }
        }
	}
	
	private void writeStatic(Writer writer, CommandDescriptor cd) throws IOException {

		writeNewLine(writer);
		writer.write("    private static final ImmutableMap<String, ParserConsumer<" + cd.simpleName() + "Builder>> FIELDS;");
		writeNewLine(writer);
		writer.write("    static {");
		writeNewLine(writer);
		writer.write("        FIELDS = ImmutableMap.<String, ParserConsumer<"+ cd.simpleName() + "Builder>>builder()");
		writeNewLine(writer);
		for (CommandPropertyDescriptor cpd : cd.properties()) {
		    writer.write("				.put(\"" + cpd.name() + "\", (parser, builder) -> builder.with" + Helper.firstToUpperCase(cpd.name()) + "(");
			if ("java.lang.String".equals(cpd.getter().getReturnType().toString())) {
				writer.write("parser.nextTextValue()");
			} else if ("int".equals(cpd.getter().getReturnType().toString())) {
				writer.write("parser.nextIntValue(0)");
			} else if ("int".equals(cpd.getter().getReturnType().toString())) {
				writer.write("parser.nextLongValue(0l)");
			} else if (OffsetDateTime.class.getName().equals(cpd.getter().getReturnType().toString())) {
				writer.write("parseOffsetDateTime(parser.nextTextValue())");
			} else if (LocalDate.class.getName().equals(cpd.getter().getReturnType().toString())) {
                writer.write("parseLocalDate(parser.nextTextValue())");
            } else if (LocalTime.class.getName().equals(cpd.getter().getReturnType().toString())) {
                writer.write("parseLocalTime(parser.nextTextValue())");
            } else {
			    throw new UnsupportedOperationException("Type not supported [" + cpd.getter().getReturnType().toString() + "]");
			}
			writer.write("))");
			writeNewLine(writer);
		}
		
		writer.write("                .build();");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);

	}

	
	private static void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
		writeNewLine(writer);
		writer.write("    " + descriptor.simpleName() +"StdDeserializer");
		writer.write("() {");
		writeNewLine(writer);
		writer.write("        super("+ descriptor.simpleName()+".class);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	/*
	private void writeMethod(Writer writer, CommandDescriptor cd) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public " + cd.simpleName() + " deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {");


		writeNewLine(writer);
		writer.write("        " + cd.simpleName() + " command = CommandFactory.new" + cd.simpleName() + "();");
		
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
		writer.write("            ParserConsumer<" + cd.simpleName() + "> consumer = FIELDS.get(p.currentName());");
		writeNewLine(writer);
		writer.write("            if (consumer == null) {");
		writeNewLine(writer);
		writer.write("                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of(\"field\",p.currentName(),\"command\", \""+ cd.simpleName()+"\"));");
		writeNewLine(writer);
		writer.write("            }");
		writeNewLine(writer);
		writer.write("            consumer.accept(p, command);");
		writeNewLine(writer);
		writer.write("            p.nextToken();");
		writeNewLine(writer);
		writer.write("        }");

		writeNewLine(writer);
		writer.write("        return command;");
		writeNewLine(writer);
		writer.write("     }");
		writeNewLine(writer);
	}
*/
	
	private void writeMethod(Writer writer, CommandDescriptor cd) throws IOException {
		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public " + cd.simpleName() + " deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {");


		writeNewLine(writer);
		writer.write("        " + cd.simpleName() + "Builder builder = new " + cd.simpleName() + "Builder();");
		
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
		writer.write("            ParserConsumer<" + cd.simpleName() + "Builder> consumer = FIELDS.get(p.currentName());");
		writeNewLine(writer);
		writer.write("            if (consumer == null) {");
		writeNewLine(writer);
		writer.write("                throw new DeserializerException(DeserializerException.Type.FIELD_NOT_FOUND, ImmutableMap.of(\"field\",p.currentName(),\"eventPayload\", \""+ cd.simpleName()+"\"));");
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