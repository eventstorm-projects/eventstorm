package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadJacksonModuleGenerator {

	private final Logger logger;

	EventPayloadJacksonModuleGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadJacksonModuleGenerator.class);
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

			JavaFileObject object = env.getFiler().createSourceFile(pack + ".json.EventPayloadModule");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".json");
		    writeConstructor(writer, descriptors);

			writer.write("}");
			writer.close();
	}

	private static void writeHeader(Writer writer, String pack) throws IOException {
		writePackage(writer, pack);

		writeNewLine(writer);
		writeNewLine(writer);
		writer.write("import " + SimpleModule.class.getName() + ";");

		writeGenerated(writer, EventPayloadJacksonModuleGenerator.class.getName());
		writer.write("public final class EventPayloadModule extends SimpleModule {");
		writeNewLine(writer);
	}

	
	
	private static void writeConstructor(Writer writer, ImmutableList<EventDescriptor> descriptors) throws IOException {
		writeNewLine(writer);
		writer.write("    public EventPayloadModule() {");
		writeNewLine(writer);
		writer.write("        super();");
		writeNewLine(writer);
		for (EventDescriptor ed : descriptors) {
			// addDeserializer(CreateUserCommand.class, new CreateUserCommandStdDeserializer());
			writer.write("        addDeserializer(" + ed.fullyQualidiedClassName() + ".class, new " + ed.simpleName() + "StdDeserializer());");
			writeNewLine(writer);
		}
		
		writer.write("    }");
		writeNewLine(writer);
	}


}