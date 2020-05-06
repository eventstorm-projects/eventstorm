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
import eu.eventstorm.core.json.Deserializer;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadDeserializersGenerator {

	private final Logger logger;

	EventPayloadDeserializersGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadDeserializersGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
//		sourceCode.forEachEventPackage((pack, list) -> {
//			try {
//				generate(processingEnvironment, pack, list);
//			} catch (Exception cause) {
//				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
//			}
//		});

	}

	private void generate(ProcessingEnvironment env, String pack, ImmutableList<EventDescriptor> descriptors) throws IOException {

	    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".io.Deserializers") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".io.Deserializers" + "]");
            return;
        }
		JavaFileObject object = env.getFiler().createSourceFile(pack + ".io.Deserializers");
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
		writer.write("import " + Deserializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ObjectMapper.class.getName() + ";");
		for (EventDescriptor ed : descriptors) {
			writeNewLine(writer);
			writer.write("import " + ed.fullyQualidiedClassName() + ";");
		}
		writeGenerated(writer, EventPayloadDeserializersGenerator.class.getName());
		writer.write("public final class Deserializers {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer) throws IOException {
		writeNewLine(writer);
		writer.write("    private Deserializers() {");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

	private void writeMethod(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    public static Deserializer<" + ed.simpleName() + "> " + Helper.propertyName(ed.simpleName()) +"Deserializer(ObjectMapper objectMapper) {");

		writeNewLine(writer);
		writer.write("        return new " + ed.simpleName() + "Deserializer(objectMapper);");
		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}