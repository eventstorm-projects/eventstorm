package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.EventPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadFactoryGenerator {

	private final Logger logger;

	EventPayloadFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadFactoryGenerator.class);
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
        
        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".EventPayloadFactory") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".EventPayloadFactory" + "]");
            return;
        }
        JavaFileObject object = env.getFiler().createSourceFile(pack + ".EventPayloadFactory");
        Writer writer = object.openWriter();

        writeHeader(writer, pack, descriptors);
        writeConstructor(writer);
        writeMethods(writer, descriptors);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, String pack, ImmutableList<EventDescriptor> descriptors) throws IOException {
        writePackage(writer, pack);
        
        for (EventDescriptor descriptor : descriptors) {
        	 writer.write("import ");
             writer.write(descriptor.fullyQualidiedClassName());
             writer.write(";");
             writeNewLine(writer);
        }
        
        writeGenerated(writer,EventPayloadFactoryGenerator.class.getName());
        writer.write("public final class EventPayloadFactory {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer) throws IOException {
        writeNewLine(writer);
        writer.write("    private EventPayloadFactory");
        writer.write("(){}");
        writeNewLine(writer);
    }

    private static void writeMethods(Writer writer, List<EventDescriptor> descriptors) throws IOException {
        for (EventDescriptor descriptor : descriptors) {
        	writeNewLine(writer);
            writer.write("    public static ");
            writer.write(descriptor.simpleName());
            writer.write(" new");
            writer.write(descriptor.simpleName());
            writer.write("(");
            
            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            for (EventPropertyDescriptor prop : descriptor.properties()) {
            	builder.append(prop.getter().getReturnType());
            	builder.append(' ');
            	builder.append(prop.name());
            	builder.append(", ");
            	
            	builder2.append(prop.name());
            	builder2.append(", ");
            }
            builder.deleteCharAt(builder.length() -1);
            builder.deleteCharAt(builder.length() -1);
            builder2.deleteCharAt(builder2.length() -1);
            builder2.deleteCharAt(builder2.length() -1);
            writer.write(builder.toString());
            writer.write(") {");
            
            writeNewLine(writer);
            writer.write("        return new ");
            writer.write(descriptor.simpleName());
            writer.write("Impl(");
            writer.write(builder2.toString());
            writer.write(");");
            writeNewLine(writer);
            writer.write("    }");
            writeNewLine(writer);
        }
        writeNewLine(writer);
    }


}