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

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.json.Serializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadSerializersGenerator {

	private final Logger logger;

	EventPayloadSerializersGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadSerializersGenerator.class);
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

		JavaFileObject object = env.getFiler().createSourceFile(pack + ".io.Serializers");
		Writer writer = object.openWriter();

		writeHeader(writer, pack + ".io", descriptors);
		writeConstructor(writer);
		for (EventDescriptor ed : descriptors) {
			writeMethod(writer, ed);
		}
		writer.write("}");
		writer.close();
	}


	private static void writeHeader(Writer writer, String pack, ImmutableList<EventDescriptor> descriptors) throws IOException {
		writePackage(writer, pack);

		writeNewLine(writer);
		writer.write("import " + Serializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ObjectMapper.class.getName() + ";");
		for (EventDescriptor ed : descriptors) {
			writeNewLine(writer);
			writer.write("import " + ed.fullyQualidiedClassName() + ";");
		}
		writeGenerated(writer, EventPayloadSerializersGenerator.class.getName());
		writer.write("public final class Serializers {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer) throws IOException {
		writeNewLine(writer);
		writer.write("    private Serializers() {");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethod(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    public static Serializer<" + ed.simpleName() + "> " + Helper.propertyName(ed.simpleName()) +"Serializer(ObjectMapper objectMapper) {");

		writeNewLine(writer);
		writer.write("        return new " + ed.simpleName() + "Serializer(objectMapper);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}