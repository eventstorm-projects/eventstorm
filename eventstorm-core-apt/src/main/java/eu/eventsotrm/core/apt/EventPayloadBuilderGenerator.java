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
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadBuilderGenerator {

	private final Logger logger;

	EventPayloadBuilderGenerator() {
		logger = LoggerFactory.getInstance().getLogger(EventPayloadBuilderGenerator.class);
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

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Builder");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, EventDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,EventPayloadBuilderGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<EventPropertyDescriptor> descriptors) throws IOException {
    	for (EventPropertyDescriptor ppd : descriptors) {
            writer.write("    private ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write("$$;");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, EventDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethod(writer, descriptor);
        writeBuildMethod(writer, descriptor);
    }

	private static void writeBuildMethod(Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName());
        writer.write(' ');
        writer.write("build() {");
        writeNewLine(writer);
        writer.write("        return EventPayloadFactory.new" + ed.simpleName() + '(');
        writeNewLine(writer);
        for (int i = 0; i < ed.properties().size(); i++) {
            writer.write("            " + ed.properties().get(i).name() + "$$");
            if (i+1 < ed.properties().size()) {
            	writer.write(",");
            }
            writeNewLine(writer);
        }
        writer.write("            );");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
	}

	private static void writeMethod(Writer writer, EventDescriptor descriptor) throws IOException {
        for (EventPropertyDescriptor ppd : descriptor.properties()) {
            writeMethod(writer, descriptor, ppd);
        }
    }

    private static void writeMethod(Writer writer,EventDescriptor ed, EventPropertyDescriptor epd) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName() + "Builder");
        writer.write(" with");
        writer.write(Helper.firstToUpperCase(epd.name()));
        writer.write("(");
        writer.write(epd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(epd.name());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this." + epd.name() + "$$ = " + epd.name() + ";");
        writeNewLine(writer);
        writer.write("        return this;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }
    
}