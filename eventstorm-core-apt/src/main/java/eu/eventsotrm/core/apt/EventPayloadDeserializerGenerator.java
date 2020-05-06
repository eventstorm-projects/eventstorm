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
import eu.eventstorm.core.json.Deserializer;
import eu.eventstorm.core.json.DeserializerException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadDeserializerGenerator {

	private final Logger logger;

	EventPayloadDeserializerGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadDeserializerGenerator.class);
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

		for (EventDescriptor ed : descriptors) {
		    
	        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
	        if (env.getElementUtils().getTypeElement(pack + ".io." + ed.simpleName() + "Deserializer") != null) {
	            logger.info("Java SourceCode already exist [" + pack + ".io." + ed.simpleName() + "Deserializer"+ "]");
	            return;
	        }
	        
	        
			JavaFileObject object = env.getFiler().createSourceFile(pack + ".io." + ed.simpleName() + "Deserializer");
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
		writer.write("import " + Deserializer.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + DeserializerException.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ImmutableMap.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + ObjectMapper.class.getName() + ";");
		writeNewLine(writer);
		writer.write("import " + descriptor.fullyQualidiedClassName() + ";");
		writeNewLine(writer);
		
		writeGenerated(writer, EventPayloadDeserializerGenerator.class.getName());
		writer.write("final class "+ descriptor.simpleName() +"Deserializer implements Deserializer<"+ descriptor.simpleName() +"> {");
		writeNewLine(writer);
	}
	
	private static void writeConstructor(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("    " + ed.simpleName() +"Deserializer");
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
		writer.write("    public " + ed.simpleName() + " deserialize(byte[] content) {");

		writeNewLine(writer);
		writer.write("        try {");
		writeNewLine(writer);
		writer.write("            return objectMapper.readValue(content, " + ed.simpleName() + ".class);");
		writeNewLine(writer);
		writer.write("        } catch (IOException cause) {");
		writeNewLine(writer);
		writer.write("            throw new DeserializerException(DeserializerException.Type.PARSE_ERROR, ImmutableMap.of(\"content\",content));");
		writeNewLine(writer);
		writer.write("        }");
		writeNewLine(writer);
		
		writer.write("     }");
		writeNewLine(writer);
	}

}