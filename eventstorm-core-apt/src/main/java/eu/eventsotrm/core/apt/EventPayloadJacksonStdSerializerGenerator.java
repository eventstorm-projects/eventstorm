package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.EventPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadJacksonStdSerializerGenerator {

	private final Logger logger;

	EventPayloadJacksonStdSerializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadJacksonStdSerializerGenerator.class);
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
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".json." + ed.simpleName() + "StdSerializer");
			Writer writer = object.openWriter();

			writeHeader(writer, pack + ".json", ed);
		    writeConstructor(writer, ed);
			writeMethod(writer, ed);

			writer.write("}");
			writer.close();
		}

	}

	private static void writeHeader(Writer writer, String pack, EventDescriptor descriptor) throws IOException {
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
		writer.write("import " + descriptor.fullyQualidiedClassName() + "Builder;");
		writeNewLine(writer);
		
		writeGenerated(writer, EventPayloadJacksonStdSerializerGenerator.class.getName());
		writer.write("final class "+ descriptor.simpleName() +"StdSerializer extends StdSerializer<"+ descriptor.simpleName() +"> {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    " + ed.simpleName() +"StdSerializer");
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
		writer.write("    public void serialize(" + ed.simpleName() + " payload, JsonGenerator gen, SerializerProvider provider) throws IOException {");
		writeNewLine(writer);
		
		for (EventPropertyDescriptor epd : ed.properties()) {

			if ("java.lang.String".equals(epd.getter().getReturnType().toString())) {
				writer.write("        gen.writeStringField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
				writeNewLine(writer);	
			} else if ("int".equals(epd.getter().getReturnType().toString())) {
				writer.write("        gen.writeNumberField(\"" + epd.name() + "\", payload."+ epd.getter().getSimpleName() +"());");
				writeNewLine(writer);	
			} else {
				writer.write("        // write (" + epd.name() + ");");
				writeNewLine(writer);
			}
			
		}

		writeNewLine(writer);
		writer.write("    }");
		writeNewLine(writer);
	}

}