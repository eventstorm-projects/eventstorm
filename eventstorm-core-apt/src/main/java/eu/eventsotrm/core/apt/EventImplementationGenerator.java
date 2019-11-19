package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.EventPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventImplementationGenerator {

	private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

	private final Logger logger;

	EventImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventImplementationGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        sourceCode.forEachEvent(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, EventDescriptor descriptor) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Impl");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);
        writeToStringBuilder(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, EventDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,EventImplementationGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write(" implements ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(");
        
        StringBuilder builder = new StringBuilder();
    	for (EventPropertyDescriptor ppd : descriptor.properties()) {
    		builder.append(ppd.getter().getReturnType().toString());
    		builder.append(" ");
    		builder.append(ppd.variable());
    		builder.append(",");
        }
    	
    	builder.deleteCharAt(builder.length() -1);
    	writer.write(builder.toString());
    	writer.write(") {");
    	writeNewLine(writer);
        
    	for (EventPropertyDescriptor ppd : descriptor.properties()) {
    		 writer.write("        this.");
            writer.write(ppd.variable());
            writer.write(" = ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    	
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<EventPropertyDescriptor> descriptors) throws IOException {
    	for (EventPropertyDescriptor ppd : descriptors) {
            writer.write("    private final ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.properties());
    }

	private static void writeMethods(Writer writer, List<EventPropertyDescriptor> descriptors) throws IOException {
        for (EventPropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, EventPropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        return this.");
        writer.write(ppd.variable());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }
    
    private static void writeToStringBuilder(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        " + TO_STRING_BUILDER + " builder = new " + TO_STRING_BUILDER + "(this);");
        writeNewLine(writer);
       
        for (EventPropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\", this.");
            writer.write(ppd.variable());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}