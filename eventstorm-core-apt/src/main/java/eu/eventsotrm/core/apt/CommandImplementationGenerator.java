package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandImplementationGenerator {

	private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

	private final Logger logger;

	CommandImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandImplementationGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEachCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

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


    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,CommandImplementationGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write(" implements ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(){}");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<CommandPropertyDescriptor> descriptors) throws IOException {
        for (CommandPropertyDescriptor ppd : descriptors) {
            writer.write("    private ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.properties());
    }

    private static void writeMethods(Writer writer, List<CommandPropertyDescriptor> descriptors) throws IOException {
        for (CommandPropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
            writeSetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, CommandPropertyDescriptor ppd) throws IOException {
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

    private static void writeSetter(Writer writer, CommandPropertyDescriptor ppd) throws IOException {

        if (ppd.setter() == null) {
            return;
        }

        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ppd.setter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.setter().getSimpleName().toString());
        writer.write("(");
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(" ");
        writer.write(ppd.variable());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this.");
        writer.write(ppd.variable());
        writer.write(" = ");
        writer.write(ppd.variable());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

    }

    private static void writeToStringBuilder(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        " + TO_STRING_BUILDER + " builder = new " + TO_STRING_BUILDER + "(this);");
        writeNewLine(writer);
       
        for (CommandPropertyDescriptor ppd : descriptor.properties()) {
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