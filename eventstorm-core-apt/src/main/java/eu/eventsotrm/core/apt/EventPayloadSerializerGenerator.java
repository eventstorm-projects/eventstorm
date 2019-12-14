package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.json.Serializer;
import eu.eventstorm.core.json.SerializerException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadSerializerGenerator {

	private final Logger logger;

	EventPayloadSerializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadSerializerGenerator.class);
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
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".io." + ed.simpleName() + "Serializer");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".io", ed);
			writeVariables(writer, ed);
			writeConstructor(writer, ed);
			writeMethod(writer, ed);

			writer.write("}");
			writer.close();
		}

	}

	private void writeVariables(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    private final ObjectMapper objectMapper;");
		writeNewLine(writer);

	}

	private static void writeHeader(Writer writer, String pack, EventDescriptor descriptor) throws IOException {
		writePackage(writer, pack);

		writeNewLine(writer);
		writer.write("import " + IOException.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + Serializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + SerializerException.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ObjectMapper.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, EventPayloadSerializerGenerator.class.getName());
		writer.write("final class "+ descriptor.simpleName() +"Serializer implements Serializer<"+ descriptor.simpleName() +"> {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    " + ed.simpleName() +"Serializer");
		writer.write("("+ObjectMapper.class.getSimpleName()+" objectMapper) {");
		writeNewLine(writer);
		writer.write("        this.objectMapper = objectMapper;");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethod(Writer writer, EventDescriptor ed) throws IOException {

		writeNewLine(writer);
		writer.write("    @Override");
		writeNewLine(writer);
		writer.write("    public byte[] serialize(" +  ed.simpleName()+" payload) {");

		writeNewLine(writer);
		writer.write("        try {");
		writeNewLine(writer);
		writer.write("            return objectMapper.writeValueAsBytes(payload);");
		writeNewLine(writer);
		writer.write("        } catch (IOException cause) {");
		writeNewLine(writer);
		writer.write("            throw new SerializerException(SerializerException.Type.WRITE_ERROR, ImmutableMap.of(\"payload\", payload));");
		writeNewLine(writer);
		writer.write("        }");
		writeNewLine(writer);
		
		writer.write("     }");
		writeNewLine(writer);
	}

}